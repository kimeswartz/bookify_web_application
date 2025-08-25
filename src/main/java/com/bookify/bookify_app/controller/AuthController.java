package com.bookify.bookify_app.controller;

// ********************************************************************************************
// * AuthController exposes REST endpoints for authentication and user management.            *
// * It supports login, logout, registration, CSRF token retrieval, and fetching user info.   *
// * Uses Spring Security’s AuthenticationManager and SecurityContext for session handling.   *
// ********************************************************************************************

import com.bookify.bookify_app.model.User;
import com.bookify.bookify_app.service.UserService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

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
        return ResponseEntity.ok(Map.of(
                "email", u.getEmail(),
                "roles", u.getRoles()
        ));
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

    // Request/response DTOs as Java records
    public record LoginRequest(String email, String password) {}
    public record RegisterRequest(String email, String password) {}
    public record MeResponse(String email, List<String> roles) {}
}