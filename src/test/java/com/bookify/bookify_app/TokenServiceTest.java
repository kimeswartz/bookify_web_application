package com.bookify.bookify_app;

// ********************************************************************************************
// * TokenServiceTest verifies the behavior of TokenService for both verification and         *
// * password reset tokens.                                                                   *
// *                                                                                          *
// * Test setup:                                                                              *
// *  - Uses Mockito mocks for VerificationTokenRepository and PasswordResetTokenRepository.  *
// *  - TokenService is created with these mocks (no database required).                      *
// *                                                                                          *
// * Covered scenarios:                                                                       *
// *  - Verification tokens: creation, validation of valid/expired tokens, mark as used.      *
// *  - Password reset tokens: creation, validation of valid/used tokens, mark as used.       *
// *                                                                                          *
// * WHY: Ensures secure token lifecycle (randomness, expiry, single-use) before integrating  *
// * with persistence or controllers.                                                         *
// ********************************************************************************************

import com.bookify.bookify_app.model.PasswordResetToken;
import com.bookify.bookify_app.model.VerificationToken;
import com.bookify.bookify_app.repository.PasswordResetTokenRepository;
import com.bookify.bookify_app.repository.VerificationTokenRepository;
import com.bookify.bookify_app.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    private VerificationTokenRepository verificationRepo;
    private PasswordResetTokenRepository resetRepo;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        resetRepo = mock(PasswordResetTokenRepository.class);
        verificationRepo = mock(VerificationTokenRepository.class);
        tokenService = new TokenService(resetRepo, verificationRepo);
    }

    // --- Verification token tests ---

    @Test
    void shouldCreateVerificationTokenWithFutureExpiry() {
        String userId = "user123";
        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);

        when(verificationRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        VerificationToken token = tokenService.createVerificationToken(userId, 15);

        verify(verificationRepo).save(captor.capture());
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getExpiresAt()).isAfter(Instant.now());
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    void shouldValidateValidVerificationToken() {
        VerificationToken token = new VerificationToken("tok123", "user123", Instant.now().plusSeconds(60));
        when(verificationRepo.findByToken("tok123")).thenReturn(Optional.of(token));

        Optional<VerificationToken> result = tokenService.validateVerificationToken("tok123");

        assertThat(result).isPresent();
    }

    @Test
    void shouldRejectExpiredVerificationToken() {
        VerificationToken token = new VerificationToken("tok123", "user123", Instant.now().minusSeconds(60));
        when(verificationRepo.findByToken("tok123")).thenReturn(Optional.of(token));

        Optional<VerificationToken> result = tokenService.validateVerificationToken("tok123");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMarkVerificationTokenAsUsed() {
        VerificationToken token = new VerificationToken("tok123", "user123", Instant.now().plusSeconds(60));
        tokenService.markVerificationTokenUsed(token);

        assertThat(token.isUsed()).isTrue();
        verify(verificationRepo).save(token);
    }

    // --- Password reset token tests ---

    @Test
    void shouldCreatePasswordResetTokenWithFutureExpiry() {
        String userId = "user123";
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);

        when(resetRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetToken token = tokenService.createPasswordResetToken(userId, 30);

        verify(resetRepo).save(captor.capture());
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getToken()).isNotBlank();
        assertThat(token.getExpiresAt()).isAfter(Instant.now());
        assertThat(token.isUsed()).isFalse();
    }

    @Test
    void shouldValidateValidPasswordResetToken() {
        PasswordResetToken token = new PasswordResetToken("res123", "user123", Instant.now().plusSeconds(60));
        when(resetRepo.findByToken("res123")).thenReturn(Optional.of(token));

        Optional<PasswordResetToken> result = tokenService.validatePasswordResetToken("res123");

        assertThat(result).isPresent();
    }

    @Test
    void shouldRejectUsedResetToken() {
        PasswordResetToken token = new PasswordResetToken("res123", "user123", Instant.now().plusSeconds(60));
        token.setUsed(true);
        when(resetRepo.findByToken("res123")).thenReturn(Optional.of(token));

        Optional<PasswordResetToken> result = tokenService.validatePasswordResetToken("res123");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMarkResetTokenAsUsed() {
        PasswordResetToken token = new PasswordResetToken("res123", "user123", Instant.now().plusSeconds(60));
        tokenService.markPasswordResetTokenUsed(token);

        assertThat(token.isUsed()).isTrue();
        verify(resetRepo).save(token);
    }
}


