package com.backbase.service.auth;

import com.backbase.buildingblocks.authentication.core.EnableDefaultAuthEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDefaultAuthEndpoint
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}