package com.acea.backend.service;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;
import com.acea.backend.exception.*;
import com.acea.backend.mapper.UserMapper;
import com.acea.backend.security.JwtService;
import com.acea.backend.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Core authentication service handling all auth business logic.
 *
 * <p>Responsibilities:
 * - User registration + email verification
 * - Login with brute-force protection
 * - JWT access token issuance
 * - Refresh token rotation
 * - Logout (single session + all sessions)
 * - Forgot / Reset password flow
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository                    userRepository;
    private final RefreshTokenRepository            refreshTokenRepository;
    private final EmailVerificationTokenRepository  emailVerificationTokenRepo;
    private final PasswordResetTokenRepository      passwordResetTokenRepo;

    private final JwtService         jwtService;
    private final PasswordEncoder    passwordEncoder;
    private final AuthenticationManager authManager;
    private final EmailService       emailService;
    private final UserMapper         userMapper;
    private final AppProperties      appProperties;

    // ── Register ──────────────────────────────────────────────────────────

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase().trim())) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS,
                "An account with this email already exists");
        }

        User user = User.builder()
            .email(request.email().toLowerCase().trim())
            .firstName(request.firstName().trim())
            .lastName(request.lastName().trim())
            .phone(request.phone())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.STUDENT)
            .emailVerified(false)
            .active(true)
            .build();

        user = userRepository.save(user);
        log.info("New user registered: [{}] id=[{}]", user.getEmail(), user.getId());

        sendEmailVerification(user);
        return userMapper.toResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email = request.email().toLowerCase().trim();

        // Load user first to check lock status before auth attempt
        User user = userRepository.findActiveByEmail(email)
            .orElseThrow(() -> new AuthenticationException("Invalid credentials") {});

        checkAccountLock(user);

        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
            );
        } catch (BadCredentialsException e) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException();
        } catch (DisabledException e) {
            throw new AccountDisabledException();
        } catch (LockedException e) {
            throw new AccountLockedException();
        }

        // Successful authentication
        user.recordSuccessfulLogin();
        userRepository.save(user);

        String sessionId    = UUID.randomUUID().toString();
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = createRefreshToken(user, sessionId, httpRequest);

        log.info("User logged in: [{}] session=[{}]", user.getEmail(), sessionId);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(appProperties.jwt().accessTokenExpiryMs())
            .user(userMapper.toResponse(user))
            .build();
    }

    // ── Refresh Token ─────────────────────────────────────────────────────

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenHash = TokenUtil.sha256Hash(request.refreshToken());

        RefreshToken stored = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN,
                "Refresh token not found or already revoked"));

        if (!stored.isActive()) {
            // Token reuse detected — revoke all user tokens (security response)
            if (stored.isRevoked()) {
                revokeAllUserTokens(stored.getUser().getId(), "TOKEN_REUSE_DETECTED");
                log.warn("Refresh token reuse detected for user [{}] — all tokens revoked",
                    stored.getUser().getEmail());
            }
            throw new InvalidTokenException(ErrorCode.REFRESH_TOKEN_EXPIRED, "Refresh token is no longer valid");
        }

        User user = stored.getUser();

        // Rotate: revoke old, issue new
        stored.revoke("ROTATED");
        refreshTokenRepository.save(stored);

        String sessionId    = stored.getSessionId(); // Keep same session
        String newRefresh   = createRefreshToken(user, sessionId, null);
        String newAccess    = jwtService.generateAccessToken(user);

        log.debug("Token rotated for user [{}] session=[{}]", user.getEmail(), sessionId);

        return AuthResponse.builder()
            .accessToken(newAccess)
            .refreshToken(newRefresh)
            .tokenType("Bearer")
            .expiresIn(appProperties.jwt().accessTokenExpiryMs())
            .user(userMapper.toResponse(user))
            .build();
    }

    // ── Logout ────────────────────────────────────────────────────────────

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) return;
        String tokenHash = TokenUtil.sha256Hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(rt -> {
            rt.revoke("LOGOUT");
            refreshTokenRepository.save(rt);
            log.info("User logged out: [{}]", rt.getUser().getEmail());
        });
    }

    public void logoutAll(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId, Instant.now(), "LOGOUT_ALL");
        log.info("All sessions revoked for user [{}]", userId);
    }

    // ── Email Verification ────────────────────────────────────────────────

    public void verifyEmail(String token) {
        String tokenHash = TokenUtil.sha256Hash(token);

        EmailVerificationToken evt = emailVerificationTokenRepo.findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_VERIFICATION_TOKEN,
                "Verification token is invalid or has already been used"));

        if (!evt.isValid()) {
            throw new InvalidTokenException(ErrorCode.VERIFICATION_TOKEN_EXPIRED,
                "Verification link has expired. Please request a new one.");
        }

        evt.markUsed();
        emailVerificationTokenRepo.save(evt);
        userRepository.markEmailVerified(evt.getUser().getId());

        log.info("Email verified for user [{}]", evt.getUser().getEmail());
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findActiveByEmail(email.toLowerCase().trim())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_VERIFIED,
                "Email is already verified");
        }
        sendEmailVerification(user);
    }

    // ── Forgot Password ───────────────────────────────────────────────────

    /**
     * Initiates password reset. Always returns success to prevent user enumeration.
     */
    public void forgotPassword(ForgotPasswordRequest request, String clientIp) {
        userRepository.findActiveByEmail(request.email().toLowerCase().trim())
            .ifPresent(user -> {
                // Invalidate any previous active reset tokens
                passwordResetTokenRepo.invalidatePreviousTokens(user.getId());

                String rawToken = TokenUtil.generateSecureToken();
                PasswordResetToken prt = PasswordResetToken.builder()
                    .user(user)
                    .tokenHash(TokenUtil.sha256Hash(rawToken))
                    .requestedFromIp(clientIp)
                    .expiresAt(Instant.now().plus(
                        appProperties.auth().passwordResetExpiryHours(), ChronoUnit.HOURS))
                    .build();

                passwordResetTokenRepo.save(prt);

                String resetLink = appProperties.frontendUrl()
                    + "/reset-password?token=" + rawToken;
                emailService.sendPasswordResetEmail(user, resetLink);
                log.info("Password reset email sent to [{}]", user.getEmail());
            });
        // No error if email not found — security by obscurity
    }

    // ── Reset Password ────────────────────────────────────────────────────

    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = TokenUtil.sha256Hash(request.token());

        PasswordResetToken prt = passwordResetTokenRepo.findByTokenHash(tokenHash)
            .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_RESET_TOKEN,
                "Password reset token is invalid or has already been used"));

        if (!prt.isValid()) {
            throw new InvalidTokenException(ErrorCode.RESET_TOKEN_EXPIRED,
                "Password reset link has expired. Please request a new one.");
        }

        User user = prt.getUser();

        // Prevent reuse of same password
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorCode.SAME_PASSWORD,
                "New password must be different from your current password");
        }

        prt.markUsed();
        passwordResetTokenRepo.save(prt);
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(request.newPassword()));

        // Revoke all refresh tokens — force re-login for security
        revokeAllUserTokens(user.getId(), "PASSWORD_CHANGED");

        emailService.sendPasswordChangedNotification(user);
        log.info("Password reset completed for user [{}]", user.getEmail());
    }

    // ── Private Helpers ───────────────────────────────────────────────────

    private String createRefreshToken(User user, String sessionId, HttpServletRequest request) {
        String rawToken = TokenUtil.generateSecureToken();

        RefreshToken rt = RefreshToken.builder()
            .user(user)
            .sessionId(sessionId)
            .tokenHash(TokenUtil.sha256Hash(rawToken))
            .deviceInfo(request != null ? request.getHeader("User-Agent") : null)
            .ipAddress(request != null ? extractClientIp(request) : null)
            .expiresAt(Instant.now().plusMillis(appProperties.jwt().refreshTokenExpiryMs()))
            .build();

        refreshTokenRepository.save(rt);
        return rawToken;
    }

    private void sendEmailVerification(User user) {
        emailVerificationTokenRepo.invalidatePreviousTokens(user.getId());

        String rawToken = TokenUtil.generateSecureToken();
        EmailVerificationToken evt = EmailVerificationToken.builder()
            .user(user)
            .tokenHash(TokenUtil.sha256Hash(rawToken))
            .expiresAt(Instant.now().plus(
                appProperties.auth().emailVerificationExpiryHours(), ChronoUnit.HOURS))
            .build();

        emailVerificationTokenRepo.save(evt);

        String verifyLink = appProperties.frontendUrl()
            + "/verify-email?token=" + rawToken;
        emailService.sendVerificationEmail(user, verifyLink);
    }

    private void checkAccountLock(User user) {
        if (user.getLockedUntil() != null && Instant.now().isBefore(user.getLockedUntil())) {
            throw new AccountLockedException();
        }
    }

    private void handleFailedLogin(User user) {
        user.incrementFailedLogin();
        int maxAttempts = appProperties.auth().maxFailedAttempts();
        if (user.getFailedLoginCount() >= maxAttempts) {
            int lockMins = appProperties.auth().lockoutDurationMinutes();
            user.setLockedUntil(Instant.now().plus(lockMins, ChronoUnit.MINUTES));
            log.warn("Account locked for user [{}] after {} failed attempts", user.getEmail(), maxAttempts);
        }
        userRepository.save(user);
    }

    private void revokeAllUserTokens(UUID userId, String reason) {
        refreshTokenRepository.revokeAllByUserId(userId, Instant.now(), reason);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}
