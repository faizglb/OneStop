package com.acea.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token request payload")
public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        @Schema(description = "The opaque refresh token issued during login")
        String refreshToken
) {}
