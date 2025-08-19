package com.bookify.bookify_app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // stäng av CSRF (för enkel testning)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // tillåt ALL trafik
                );
        return http.build();
    }
}

