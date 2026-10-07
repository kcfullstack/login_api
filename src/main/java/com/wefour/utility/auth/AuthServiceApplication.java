package com.wefour.utility.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for User Authentication and OAuth2 Login Microservice.
 *
 * <p>Built with Java 21, Spring Boot 3.3.x, and Spring Security 6.x.
 * Supports OAuth2 Login (Google, GitHub), JWT Resource Server capability,
 * and Spring Data JPA persistence with H2 and MySQL support.</p>
 */
@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
