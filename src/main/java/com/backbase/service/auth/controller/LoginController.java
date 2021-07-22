package com.backbase.service.auth.controller;

import com.backbase.buildingblocks.authentication.core.AuthEndpoints;
import com.backbase.buildingblocks.authentication.core.AuthenticationHandler;
import com.backbase.buildingblocks.jwt.core.exception.JsonWebTokenException;
import com.backbase.buildingblocks.jwt.external.ExternalJwtProducer;
import com.backbase.buildingblocks.jwt.external.ExternalJwtProducerProperties;
import com.backbase.buildingblocks.jwt.external.exception.ExternalJwtException;
import com.backbase.buildingblocks.jwt.external.token.ExternalJwtCookie;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {
  private static final String USERNAME_FIELD = "username";
  private static final String EMAIL_FIELD = "email";
  private static final String PASSWORD_FIELD = "password";
  private static final String LOGIN_TEMPLATE = "login.ftl";
  private static final String SUCCESS_TEMPLATE = "success.ftl";
  private static final String DEFAULT_LOGIN_PATH = "/login";

  private final AuthenticationManager authenticationManager;
  private final AuthenticationHandler authenticationHandler;
  private final ExternalJwtProducerProperties tokenProperties;
  private final ExternalJwtProducer tokenProducer;

  @ResponseBody
  @GetMapping(value = DEFAULT_LOGIN_PATH, produces = TEXT_HTML_VALUE)
  public String getLoginPage(HttpServletRequest request) {
    return generateHtmlTemplate(request, null, LOGIN_TEMPLATE);
  }

  @ResponseBody
  @PostMapping(value = DEFAULT_LOGIN_PATH, produces = TEXT_HTML_VALUE, consumes = APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<String> doLogin(
      @RequestParam(value = USERNAME_FIELD) String username,
      @RequestParam(value = EMAIL_FIELD) String email,
      @RequestParam(value = PASSWORD_FIELD) String password,
      HttpServletRequest request,
      HttpServletResponse response
      ) {
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
    try {
      Authentication authentication = authenticationManager.authenticate(authRequest);
      if (authentication != null && authentication.isAuthenticated() &&
          !(authentication instanceof AnonymousAuthenticationToken)) {
        this.appendCookieToken(request, response, authentication);
        this.authenticationHandler.onSuccessfulLogin(request, response, authentication);
        log.debug("user {} authenticated", username);
      }
      log.info("user {} authenticated with email {}", username, email);
      return new ResponseEntity<>(generateHtmlTemplate(request, null, SUCCESS_TEMPLATE), HttpStatus.OK);
    }  catch(AuthenticationException ex) {
      log.error("Unauthorized user '{}'", username);
      return new ResponseEntity<>(generateHtmlTemplate(request, ex.getMessage(), LOGIN_TEMPLATE), HttpStatus.UNAUTHORIZED);
    }

  }

  private void appendCookieToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    log.debug("append external jwt for auth [{}]", authentication);
    try {
      ExternalJwtCookie externalJwtCookie = ExternalJwtCookie.create()
          .withProducer(tokenProducer)
          .withAuthentication(authentication)
          .withCookieName(tokenProperties.getCookie().getName())
          .withRequest(request)
          .build();
      response.addCookie(externalJwtCookie);
    } catch (JsonWebTokenException | ExternalJwtException e) {
      log.error("cannot append external JWT to http response", e);
    }
  }

  private String generateHtmlTemplate(HttpServletRequest request, String errorMessage, String loginTemplate) {
    final String loginPageUri = AuthEndpoints.getRequestPath(request);

    Configuration config = new Configuration(Configuration.VERSION_2_3_29);
    Writer out = new StringWriter();
    try {
      Resource resource = new ClassPathResource("statics");
      config.setDirectoryForTemplateLoading(resource.getFile());
      config.setDefaultEncoding(StandardCharsets.UTF_8.name());
      Template template = config.getTemplate(loginTemplate);
      template.process(inputTemplateInitialization(errorMessage, loginPageUri), out);
    } catch (IOException | TemplateException e) {
      log.error("error generating HTML template", e);
    }

    return out.toString();
  }

  private Map<String, Object> inputTemplateInitialization(String errorMessage, String loginPageUri) {
    Map<String, Object> input = new HashMap<>();
    input.put("USERNAME_FIELD", USERNAME_FIELD);
    input.put("PASSWORD_FIELD", PASSWORD_FIELD);
    input.put("EMAIL_FIELD", EMAIL_FIELD);
    input.put("LOGIN_PAGE_URL", loginPageUri);
    input.put("ERROR_MESSAGE", errorMessage);

    return input;
  }
}
