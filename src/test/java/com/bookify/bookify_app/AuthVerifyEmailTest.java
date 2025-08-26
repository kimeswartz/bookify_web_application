package com.bookify.bookify_app;

// ********************************************************************************************
// * AuthVerifyEmailTest validates the email verification flow using real Spring context.      *
// *                                                                                          *
// * Test setup:                                                                              *
// *  - Runs with @SpringBootTest + @AutoConfigureMockMvc to include full web + data layers.  *
// *  - Database is cleared before each test for isolation.                                   *
// *                                                                                          *
// * Covered scenarios:                                                                       *
// *  - Valid token: user is activated after verification.                                    *
// *  - Invalid token: verification endpoint responds gracefully, user remains inactive.      *
// *                                                                                          *
// * WHY: Ensures the end-to-end activation mechanism behaves securely and predictably,       *
// * covering both success and failure cases.                                                 *
// ********************************************************************************************

import com.bookify.bookify_app.model.User;
import com.bookify.bookify_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthVerifyEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void shouldActivateUserAfterEmailVerification() throws Exception {
        // --- Step 1: Register a new user ---
        String email = "verifytester@example.com";
        String password = "Secret123!";

        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract token from JSON (simple split; JSON parser would be more robust)
        String token = response.split("\"verificationToken\":\"")[1].split("\"")[0];

        // --- Step 2: Verify the email with the token ---
        mockMvc.perform(get("/api/v1/auth/verify-email")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("If the token was valid, the account has been activated."));

        // --- Step 3: Confirm user is active in DB ---
        Optional<User> userOpt = userRepository.findByEmail(email);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().isActive()).isTrue();
    }

    @Test
    void shouldNotActivateUserWithInvalidToken() throws Exception {
        // --- Step 1: Register a new user ---
        String email = "invalidtokentest@example.com";
        String password = "Secret123!";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "%s",
                                    "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationToken").exists());

        // --- Step 2: Try verifying with an invalid token ---
        mockMvc.perform(get("/api/v1/auth/verify-email")
                        .param("token", "this-is-not-a-real-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("If the token was valid, the account has been activated."));

        // --- Step 3: Ensure user remains inactive ---
        Optional<User> userOpt = userRepository.findByEmail(email);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().isActive()).isFalse();
    }
}

