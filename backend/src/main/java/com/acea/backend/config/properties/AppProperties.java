package com.acea.backend.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Type-safe application configuration properties.
 * Bound from application.yml under the "app" prefix.
 */
@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank String frontendUrl,
        @NotBlank String supportEmail,
        @NotBlank String aiServiceUrl,
        @NotNull JwtProperties jwt,
        @NotNull AuthProperties auth,
        @NotNull CorsProperties cors
) {

    public record JwtProperties(
            @NotBlank String secret,
            @NotNull @Positive Long accessTokenExpiryMs,
            @NotNull @Positive Long refreshTokenExpiryMs,
            @NotBlank String issuer,
            @NotBlank String audience
    ) {}

    public record AuthProperties(
            @NotNull @Positive Integer emailVerificationExpiryHours,
            @NotNull @Positive Integer passwordResetExpiryHours,
            @NotNull @Positive Integer maxFailedAttempts,
            @NotNull @Positive Integer lockoutDurationMinutes,
            @NotNull @Positive Integer bcryptStrength
    ) {}

    public record CorsProperties(
            @NotBlank String allowedOrigins,
            @NotBlank String allowedMethods,
            @NotBlank String allowedHeaders,
            boolean allowCredentials,
            long maxAge
    ) {}
}
