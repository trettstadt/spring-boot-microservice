package de.trettstadt.microservices.springbootmicroservice.common.config;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.jwt.JwtClaimNames.AUD;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.web.SecurityFilterChain;

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
    return new JwtClaimValidator<List<String>>(AUD,
        aud -> aud.contains("spring-boot-microservice"));
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(withDefaults())
        )
        .oauth2Client(withDefaults());
    return http.build();
  }
}
