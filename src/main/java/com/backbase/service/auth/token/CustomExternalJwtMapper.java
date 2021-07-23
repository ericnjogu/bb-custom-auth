package com.backbase.service.auth.token;

import com.backbase.buildingblocks.jwt.external.ExternalJwtMapper;
import com.backbase.buildingblocks.jwt.external.token.ExternalJwtClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class CustomExternalJwtMapper implements ExternalJwtMapper {

  public static final String USR_COUNTRY = "usr_country";

  @Override
  public Optional<ExternalJwtClaimsSet> claimSet(Authentication authentication, HttpServletRequest request) {
    log.info("adding extra claims to external tokens");
    String country = request.getLocale().getCountry();
    return Optional.of(new ExternalJwtClaimsSet(Collections.singletonMap(USR_COUNTRY, country)));
  }
}
