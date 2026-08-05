package com.acea.backend.dto.response;

import com.acea.backend.domain.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Public-facing user profile in API responses.
 * Never exposes password hash or sensitive security fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User profile information")
public record UserResponse(

        @Schema(description = "Unique user identifier")
        UUID id,

        @Schema(description = "User's first name", example = "Rahul")
        String firstName,

        @Schema(description = "User's last name", example = "Sharma")
        String lastName,

        @Schema(description = "User's full name", example = "Rahul Sharma")
        String fullName,

        @Schema(description = "User's email address", example = "rahul@example.com")
        String email,

        @Schema(description = "User's phone number")
        String phone,

        @Schema(description = "URL to user's avatar image")
        String avatarUrl,

        @Schema(description = "User's assigned role")
        Role role,

        @Schema(description = "Account tier: FREE, PREMIUM, ENTERPRISE")
        String accountTier,

        @Schema(description = "Whether the email has been verified")
        boolean emailVerified,

        @Schema(description = "Whether the account is active")
        boolean active,

        @Schema(description = "Preferred locale", example = "en-IN")
        String locale,

        @Schema(description = "Preferred timezone", example = "Asia/Kolkata")
        String timezone,

        @Schema(description = "Timestamp of last login")
        Instant lastLoginAt,

        @Schema(description = "Account creation timestamp")
        Instant createdAt
) {}
