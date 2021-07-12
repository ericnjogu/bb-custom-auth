package com.backbase.service.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class})
class SecureApplicationTest {

    @Autowired
    private ApplicationContext context;

    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void init() {
        this.authenticationManager = this.context.getBean(AuthenticationManager.class);
    }

    @Test
    void shouldBeAuthenticatedAsAdmin() {
        assertTrue(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("admin", "admin")).isAuthenticated());
    }

    @Test
    void shouldThrowBadCredentialsException() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("admin",
            "wrong password");
        assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(authentication));
    }

    @AfterEach
    public void close() {
        SecurityContextHolder.clearContext();
    }
}