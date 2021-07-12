package com.backbase.service.auth;

import com.backbase.buildingblocks.authentication.core.AuthSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends AuthSecurityConfiguration {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
            .withUser("user").password("{noop}user").roles("USER", "group_user(USER)")
            .and()
            .withUser("admin").password("{noop}admin").roles("ADMIN", "ACTUATOR", "group_admin(ADMIN)")
            .and()
            .withUser("manager").password("{noop}manager").roles("USER", "group_manager(MANAGER)");
    }
}