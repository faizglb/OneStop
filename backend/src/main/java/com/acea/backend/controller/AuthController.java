package com.acea.backend.controller;

import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;
import com.acea.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.acea.backend.domain.entity.User;

import java.util.Map;

/**
 * Authentication REST controller.
 *
 * <p>Base path: /api/v1/auth (configured in application.yml server.servlet.context-path)
 * All endpoints here are PUBLIC (whitelisted in SecurityConfig).
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User registration, login, token management, and password operations")
public class AuthController {

    private final AuthService authService;

    // ── POST /auth/register ────────────────────────────────────────────────

    @PostMapping("/register")
    @Operation(summary = "Register a new user account",
               description = "Creates a new STUDENT account and sends an email verification link")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Validation failed")
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse user = authService.register(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(
                "Account created successfully. Please check your email to verify your account.",
                user
            ));
    }

    // ── POST /auth/login ───────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(summary = "Login with email and password",
               description = "Returns JWT access token (15 min) and refresh token (7 days)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "423", description = "Account locked")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        AuthResponse auth = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", auth));
    }

    // ── POST /auth/refresh ─────────────────────────────────────────────────

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
               description = "Exchange a valid refresh token for a new access token. Implements token rotation.")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse auth = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", auth));
    }

    // ── POST /auth/logout ─────────────────────────────────────────────────

    @PostMapping("/logout")
    @Operation(summary = "Logout current session",
               description = "Revokes the provided refresh token. Client must discard the access token.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody(required = false) Map<String, String> body) {

        String refreshToken = body != null ? body.get("refreshToken") : null;
        authService.logout(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    // ── POST /auth/logout-all ─────────────────────────────────────────────

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices",
               description = "Revokes ALL refresh tokens for the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal User currentUser) {

        authService.logoutAll(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices successfully"));
    }

    // ── GET /auth/verify-email ─────────────────────────────────────────────

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address",
               description = "Validates the token from the email verification link")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @RequestParam("token") String token) {

        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully. You can now log in."));
    }

    // ── POST /auth/resend-verification ────────────────────────────────────

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification link")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @RequestBody Map<String, String> body) {

        authService.resendVerificationEmail(body.getOrDefault("email", ""));
        return ResponseEntity.ok(ApiResponse.success(
            "If an unverified account with this email exists, a new verification link has been sent."));
    }

    // ── POST /auth/forgot-password ────────────────────────────────────────

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset",
               description = "Sends a password reset email. Always returns 200 to prevent user enumeration.")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = httpRequest.getHeader("X-Forwarded-For") != null
            ? httpRequest.getHeader("X-Forwarded-For").split(",")[0].trim()
            : httpRequest.getRemoteAddr();

        authService.forgotPassword(request, clientIp);
        return ResponseEntity.ok(ApiResponse.success(
            "If an account with this email exists, a password reset link has been sent."));
    }

    // ── POST /auth/reset-password ─────────────────────────────────────────

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token from email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
            "Password reset successfully. Please log in with your new password."));
    }

    // ── GET /auth/me ──────────────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(ApiResponse.success("User profile retrieved",
            new UserResponse(
                currentUser.getId(), currentUser.getFirstName(), currentUser.getLastName(),
                currentUser.getFullName(), currentUser.getEmail(), currentUser.getPhone(),
                currentUser.getAvatarUrl(), currentUser.getRole(), currentUser.getAccountTier(),
                currentUser.isEmailVerified(), currentUser.isActive(),
                currentUser.getLocale(), currentUser.getTimezone(),
                currentUser.getLastLoginAt(), currentUser.getCreatedAt()
            )
        ));
    }
}
