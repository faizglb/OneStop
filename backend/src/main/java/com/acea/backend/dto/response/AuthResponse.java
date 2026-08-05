package com.acea.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * Authentication response containing both JWT tokens and user profile.
 * Returned on successful login and token refresh.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Authentication response with tokens and user info")
public record AuthResponse(

        @Schema(description = "Short-lived JWT access token (15 min)", example = "eyJhbGci...")
        String accessToken,

        @Schema(description = "Opaque refresh token (7 days)", example = "ref_abc123...")
        String refreshToken,

        @Schema(description = "Token type — always 'Bearer'", example = "Bearer")
        String tokenType,

        @Schema(description = "Access token expiry in milliseconds from now", example = "900000")
        Long expiresIn,

        @Schema(description = "Authenticated user information")
        UserResponse user
) {
    // Canonical constructor to supply default tokenType
    public AuthResponse {
        if (tokenType == null) tokenType = "Bearer";
    }
}
