package com.bookify.bookify_app.service;

// ********************************************************************************************
// * TokenService manages creation, validation, and consumption of verification and            *
// * password-reset tokens. Tokens are URL-safe, random, time-bound, and single-use.          *
// *                                                                                          *
// * HOW:                                                                                     *
// * - Generate 256-bit tokens using SecureRandom, Base64 URL encoding (no padding).          *
// * - Persist tokens with expiry timestamps per use case.                                    *
// * - Validate by checking existence, unused flag, and not expired.                          *
// * - Mark tokens as used to enforce single-use semantics.                                   *
// *                                                                                          *
// * WHY: Centralizes token lifecycle with clear security properties and invariants.          *
// ********************************************************************************************

import com.bookify.bookify_app.model.VerificationToken;
import com.bookify.bookify_app.model.PasswordResetToken;
import com.bookify.bookify_app.repository.VerificationTokenRepository;
import com.bookify.bookify_app.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    // Cryptographically strong RNG for token bytes
    private static final SecureRandom RANDOM = new SecureRandom();
    // URL-safe Base64 without padding to make token copy/paste and URL friendly
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generate a URL-safe, 256-bit random token string.
     * Uses SecureRandom and Base64 URL encoding without padding.
     */

    private String generateTokenString() {
        byte[] randomBytes = new byte[32];
        RANDOM.nextBytes(randomBytes);
        return BASE64_ENCODER.encodeToString(randomBytes);
    }

    // --- Email verification tokens ---

    /**
     * Create and persist a new email verification token for a user.
     *
     * @param userId        the user identifier to bind the token to
     * @param minutesValid  validity window in minutes from now
     * @return the persisted VerificationToken
     */

    public VerificationToken createVerificationToken(String userId, int minutesValid) {
        String token = generateTokenString();
        VerificationToken verificationToken = new VerificationToken(token, userId, Instant.now().plus(minutesValid, ChronoUnit.MINUTES));
        return verificationTokenRepository.save(verificationToken);
    }

    /**
     * Validate a verification token by value.
     * A token is valid if it exists, is not used, and has not expired.
     *
     * @param token the raw token string
     * @return Optional containing the token if valid; otherwise Optional.empty()
     */

    public Optional<VerificationToken> validateVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token)
        .filter(t -> !t.isUsed() && t.getExpiresAt().isAfter(Instant.now()));
    }

    /**
     * Mark a verification token as used (single-use enforcement).
     *
     * @param token the token to consume
     */

    public void markVerificationTokenUsed(VerificationToken token) {
        token.setUsed(true);
        verificationTokenRepository.save(token);
    }

    // --- Password reset tokens ---

    /**
     * Create and persist a new password reset token for a user.
     *
     * @param userId        the user identifier to bind the token to
     * @param minutesValid  validity window in minutes from now
     * @return the persisted PasswordResetToken
     */

    public PasswordResetToken createPasswordResetToken(String userId, int minutesValid) {
        String token = generateTokenString();
        PasswordResetToken r = new PasswordResetToken(token, userId, Instant.now().plus(minutesValid, ChronoUnit.MINUTES));
        return passwordResetTokenRepository.save(r);
    }

    /**
     * Validate a password reset token by value.
     * A token is valid if it exists, is not used, and has not expired.
     *
     * @param token the raw token string
     * @return Optional containing the token if valid; otherwise Optional.empty()
     */

    public Optional<PasswordResetToken> validatePasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiresAt().isAfter(Instant.now()));
    }

    /**
     * Mark a password reset token as used (single-use enforcement).
     *
     * @param token the token to consume
     */

    public void markPasswordResetTokenUsed(PasswordResetToken token) {
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }
}
