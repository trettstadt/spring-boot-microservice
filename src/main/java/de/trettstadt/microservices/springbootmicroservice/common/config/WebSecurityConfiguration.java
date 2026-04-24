package de.trettstadt.microservices.springbootmicroservice.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;

import java.util.List;

import static org.springframework.security.oauth2.jwt.JwtClaimNames.AUD;

/**
 * Spring security configuration for REST.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    /**
     * Forcing to use separate tokens for each target service improves security.
     */
    @Bean
    OAuth2TokenValidator<Jwt> audienceValidator() {
        return new JwtClaimValidator<List<String>>(AUD, aud -> aud.contains("spring-boot-microservice"));
    }
}
