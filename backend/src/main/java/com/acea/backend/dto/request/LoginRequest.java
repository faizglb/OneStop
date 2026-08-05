package com.acea.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login request payload")
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Schema(example = "rahul@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 1, max = 128)
        @Schema(example = "SecurePass@123")
        String password
) {}
