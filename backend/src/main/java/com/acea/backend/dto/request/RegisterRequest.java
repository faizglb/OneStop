package com.acea.backend.dto.request;

import com.acea.backend.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * Request DTO for user registration.
 * Uses Java records for immutability — no setter injection possible.
 */
@Builder
@PasswordMatch(password = "password", confirmPassword = "confirmPassword")
@Schema(description = "User registration request payload")
public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name contains invalid characters")
        @Schema(description = "User's first name", example = "Rahul")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name contains invalid characters")
        @Schema(description = "User's last name", example = "Sharma")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Size(max = 320, message = "Email must not exceed 320 characters")
        @Schema(description = "User's email address", example = "rahul@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#])[A-Za-z\\d@$!%*?&_#]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
        )
        @Schema(description = "Password (min 8 chars, must include upper, lower, digit, special)", example = "SecurePass@123")
        String password,

        @NotBlank(message = "Please confirm your password")
        @Schema(description = "Password confirmation (must match password)", example = "SecurePass@123")
        String confirmPassword,

        @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Please provide a valid phone number")
        @Schema(description = "Optional phone number", example = "+91 98765 43210")
        String phone
) {}
