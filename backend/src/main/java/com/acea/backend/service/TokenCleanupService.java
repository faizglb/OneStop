package com.acea.backend.service;

import com.acea.backend.domain.repository.EmailVerificationTokenRepository;
import com.acea.backend.domain.repository.PasswordResetTokenRepository;
import com.acea.backend.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled service for housekeeping tasks.
 * Runs token cleanup jobs to prevent database bloat.
 *
 * <p>Future Sprint tasks will also be scheduled here:
 * - AI embedding refresh jobs
 * - Report generation
 * - Notification delivery
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenRepository           refreshTokenRepo;
    private final EmailVerificationTokenRepository emailVerifRepo;
    private final PasswordResetTokenRepository     passwordResetRepo;

    /**
     * Deletes expired/revoked refresh tokens older than 30 days.
     * Runs every day at 2:00 AM UTC.
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    @Transactional
    public void cleanupRefreshTokens() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS);
        refreshTokenRepo.deleteExpiredAndRevoked(cutoff);
        log.info("[TokenCleanup] Refresh token cleanup completed. Cutoff: {}", cutoff);
    }

    /**
     * Deletes expired email verification tokens.
     * Runs every day at 2:30 AM UTC.
     */
    @Scheduled(cron = "0 30 2 * * *", zone = "UTC")
    @Transactional
    public void cleanupEmailVerificationTokens() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        emailVerifRepo.deleteExpired(cutoff);
        log.info("[TokenCleanup] Email verification token cleanup completed.");
    }

    /**
     * Deletes expired password reset tokens.
     * Runs every day at 3:00 AM UTC.
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    @Transactional
    public void cleanupPasswordResetTokens() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        passwordResetRepo.deleteExpired(cutoff);
        log.info("[TokenCleanup] Password reset token cleanup completed.");
    }
}
