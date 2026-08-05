package com.acea.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Forgot password request payload")
public record ForgotPasswordRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Schema(example = "rahul@example.com")
        String email
) {}
