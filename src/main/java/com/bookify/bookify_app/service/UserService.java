package com.bookify.bookify_app.service;

// ********************************************************************************************
// * UserService handles user registration and user lookup for authentication.                *
// *                                                                                          *
// * - registerUser: creates a new user in the current tenant (clinic) with a secure          *
// *   password hash and a default role.                                                      *
// * - loadUserByUsername: adapts our User entity to Spring Security's UserDetails for login. *
// *                                                                                          *
// * WHY: Centralizes user-related business logic and the bridge to Spring Security.          *
// ********************************************************************************************

import com.bookify.bookify_app.model.User;
import com.bookify.bookify_app.repository.UserRepository;
import com.bookify.bookify_app.util.UserRole;
import com.bookify.bookify_app.filter.TenantContext;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user for the current tenant (clinic).
     * - Validates that the email is unique.
     * - Hashes the raw password using the configured PasswordEncoder.
     * - Assigns a default role (OWNER) and associates the user with the current clinicId.
     *
     * @param email       unique user email
     * @param rawPassword plaintext password provided by the user
     * @return the persisted User entity
     * @throws IllegalArgumentException if the email already exists
     */

    public User registerUser(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        User newUser = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .roles(Set.of(UserRole.OWNER)) // default: OWNER
                .clinicId(TenantContext.getClinicId())
                .build();
        return userRepository.save(newUser);
    }


    /**
     * Load a user by email and convert it into a Spring Security UserDetails instance.
     * This is used by the authentication process during login.
     *
     * @param email the username (email) to look up
     * @return a UserDetails adapted from our domain User
     * @throws UsernameNotFoundException if the user does not exist
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Map domain roles to Spring Security roles (as strings) for authorization decisions
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }
}
