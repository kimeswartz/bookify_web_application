package com.bookify.bookify_app.controller;

// ********************************************************************************************
// * AuthController exposes REST endpoints for authentication and user management.            *
// * It supports login, logout, registration, CSRF token retrieval, and fetching user info.   *
// * Uses Spring Security’s AuthenticationManager and SecurityContext for session handling.   *
// ********************************************************************************************

import com.bookify.bookify_app.model.User;
import com.bookify.bookify_app.model.VerificationToken;
import com.bookify.bookify_app.service.TokenService;
import com.bookify.bookify_app.service.UserService;
import com.bookify.bookify_app.model.PasswordResetToken;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        // Store authentication in SecurityContext and regenerate session ID
        // (protection against session fixation attacks)
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.changeSessionId();

        // Extract roles from authenticated user
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new MeResponse(auth.getName(), roles));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req) throws ServletException {
        // Logs out the current user and clears the security context
        req.logout();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        // Create and persist a new user through the UserService
        User u = userService.registerUser(req.email(), req.password());

        // Create a verification token (valid 24h)
        VerificationToken token = tokenService.createVerificationToken(u.getId(), 60 * 24);

        // In production: send email with a link containing token
        // For development, include token in response (dev only)
        return ResponseEntity.ok(Map.of(
                "email", u.getEmail(),
                "roles", u.getRoles(),
                "verificationToken", token.getToken()
        ));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String tokenValue) {
        Optional<VerificationToken> tokenOpt = tokenService.validateVerificationToken(tokenValue);

        if (tokenOpt.isPresent()) {
            VerificationToken verificationToken = tokenOpt.get();
            userService.activateUser(verificationToken.getUserId());
            tokenService.markVerificationTokenUsed(verificationToken);
        }

        // Always return the same response to avoid leaking info
        return ResponseEntity.ok(Map.of("message", "If the token was valid, the account has been activated."));
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        // Return a CSRF token so the client can include it in requests
        return Map.of("csrfToken", token.getToken());
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        // Return the authenticated user’s identity and roles
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new MeResponse(auth.getName(), roles));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        // Always return the same response to prevent user enumeration attacks
        Optional<User> userOpt = userService.findByEmail(req.email());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Create a password reset token valid for 30 minutes
            PasswordResetToken token = tokenService.createPasswordResetToken(user.getId(), 30);

            // In production: send a reset link via email.
            // For development/demo: return token directly (never expose in real systems).
            return ResponseEntity.ok(Map.of(
                    "message", "If an account exists, a password reset link has been sent.",
                    "resetToken", token.getToken() // dev only
            ));
        }

        return ResponseEntity.ok(Map.of("message", "If an account exists, a password reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        Optional<PasswordResetToken> tokenOpt = tokenService.validatePasswordResetToken(req.token());

        if (tokenOpt.isPresent()) {
            PasswordResetToken token = tokenOpt.get();
            // Update user password if token is valid
            userService.updatePassword(token.getUserId(), req.newPassword());
            // Invalidate token after successful use (single-use enforcement)
            tokenService.markPasswordResetTokenUsed(token);
        }

        // Always return the same response, regardless of validity (avoid information leaks)
        return ResponseEntity.ok(Map.of("message", "If the token was valid, the password has been reset."));
    }

    // DTOs
    public record ForgotPasswordRequest(String email) {}
    public record ResetPasswordRequest(String token, String newPassword) {}


    // Request/response DTOs as Java records
    public record LoginRequest(String email, String password) {}
    public record RegisterRequest(String email, String password) {}
    public record MeResponse(String email, List<String> roles) {}
}