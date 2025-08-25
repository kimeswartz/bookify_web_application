package com.bookify.bookify_app;

// ********************************************************************************************
// * AuthControllerIntegrationTest verifies the AuthController endpoints using MockMvc.       *
// *                                                                                          *
// * Covered flows:                                                                           *
// *  - GET /csrf returns a CSRF token JSON payload.                                          *
// *  - POST /register creates a user and returns email + roles.                              *
// *                                                                                          *
// * Test wiring:                                                                             *
// *  - @WebMvcTest loads only web layer; controller is tested in isolation.                  *
// *  - UserService and AuthenticationManager are mocked.                                     *
// *  - ClinicService is also mocked because filters may depend on it in the chain.           *
// *  - TestSecurityConfig provides a minimal security setup (CSRF cookie + permitAll).       *
// ********************************************************************************************

import com.bookify.bookify_app.controller.AuthController;
import com.bookify.bookify_app.model.User;
import com.bookify.bookify_app.service.ClinicService;
import com.bookify.bookify_app.service.UserService;
import com.bookify.bookify_app.util.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@Import(AuthControllerIntegrationTest.TestSecurityConfig.class)
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserService userService;
    @MockitoBean private AuthenticationManager authenticationManager;


    // Mocked because the filter chain may reference ClinicService (e.g., TenantSubdomainFilter)
    @MockitoBean private ClinicService clinicService;

    @Test
    void shouldReturnCsrfToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.csrfToken").exists());
    }

    @Test
    void shouldRegisterUser() throws Exception {
        User stub = User.builder()
                .email("kim@example.com")
                .passwordHash("$2a$10$stub")
                .roles(Set.of(UserRole.OWNER))
                .clinicId("clinic-123")
                .build();

        when(userService.registerUser(anyString(), anyString())).thenReturn(stub);

        String body = """
                { "email": "kim@example.com", "password": "secret123" }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("kim@example.com"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    /**
     * Minimal security configuration for tests:
     * - Enables CSRF with a cookie-based token (readable by JS for client-side inclusion).
     * - Disables CORS for simplicity.
     * - Permits all requests (authorization is not under test here).
     */

    @TestConfiguration(proxyBeanMethods = false)
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                    .cors(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
    }
}