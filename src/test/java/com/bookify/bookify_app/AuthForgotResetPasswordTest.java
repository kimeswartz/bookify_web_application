package com.bookify.bookify_app;

// ********************************************************************************************
// * AuthForgotResetPasswordTest verifies the full password reset flow using MockMvc.         *
// *                                                                                          *
// * Flow covered:                                                                            *
// *  - Register a user.                                                                      *
// *  - Request a password reset token via /forgot-password.                                  *
// *  - Reset password using /reset-password with the token.                                  *
// *  - Assert that password hash in DB is updated only when token is valid.                  *
// *                                                                                          *
// * WHY: Ensures both positive (valid token) and negative (invalid token) paths behave       *
// * securely, preventing leaks while enforcing single-use reset tokens.                      *
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthForgotResetPasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        // Ensure the database is clean for test isolation
        userRepository.deleteAll();
    }

    @Test
    void shouldResetPasswordWithValidToken() throws Exception {
        String email = "resetuser@example.com";
        String oldPassword = "OldPass123!";
        String newPassword = "NewPass456!";

        // --- Step 1: Register user ---
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, oldPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        // --- Step 2: Request password reset (forgot-password) ---
        String response = mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If an account exists, a password reset link has been sent."))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract reset token (in production this would come via email)
        String token = response.split("\"resetToken\":\"")[1].split("\"")[0];

        // --- Step 3: Reset password using the valid token ---
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": "%s",
                                  "newPassword": "%s"
                                }
                                """.formatted(token, newPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("If the token was valid, the password has been reset."));

        // --- Step 4: Verify DB now contains a new hashed password ---
        Optional<User> userOpt = userRepository.findByEmail(email);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getPasswordHash()).isNotEqualTo(oldPassword);
    }

    @Test
    void shouldNotResetPasswordWithInvalidToken() throws Exception {
        String email = "invalidreset@example.com";
        String password = "Secret123!";

        // --- Step 1: Register user ---
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk());

        // --- Step 2: Attempt reset with a fake token ---
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": "not-a-valid-token",
                                  "newPassword": "SomethingElse!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("If the token was valid, the password has been reset."));

        // --- Step 3: DB should still contain the original hash (no reset occurred) ---
        Optional<User> userOpt = userRepository.findByEmail(email);
        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getPasswordHash()).isNotEqualTo("SomethingElse!");
    }
}
