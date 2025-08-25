package com.bookify.bookify_app.config;

// ********************************************************************************************
// * SecurityConfig defines the security setup for the application using Spring Security.     *
// * It configures CSRF, CORS, request authorization, and important security headers.         *
// * Additionally, it exposes beans for AuthenticationManager, PasswordEncoder, and CORS.     *
// ********************************************************************************************

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> {})
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).preload(true))
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .referrerPolicy(ref -> ref.policy(ReferrerPolicy.NO_REFERRER))
                        .contentTypeOptions(withDefaults -> {})
                );

        return http.build();
    }

    /**
     * Exposes AuthenticationManager as a bean so it can be injected
     * (e.g., in authentication controllers or services).
     * Uses the providers automatically configured by Spring.
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Bygger på de provider(s) som Spring konfigurerar automatiskt
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder bean used for authentication and user registration.
     * BCrypt is a strong, salted hashing algorithm recommended for storing passwords.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for the application.
     * Two strategies are possible:
     * 1) Open: allows all origins without credentials (default below).
     * 2) Strict: only specific origins allowed, with credentials (commented out).
     */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // 1) (Open, best during development)
        cfg.setAllowedOriginPatterns(List.of("*"));
        cfg.setAllowCredentials(false);

        // 2) Strict (Will be turned on when app is live)
        // cfg.setAllowedOrigins(List.of("https://din-front.com", "http://localhost:5173"));
        // cfg.setAllowCredentials(true);

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "X-CSRF-TOKEN"));
        cfg.setExposedHeaders(List.of("Location"));
        cfg.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}