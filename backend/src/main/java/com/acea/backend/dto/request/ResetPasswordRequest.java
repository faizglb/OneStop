package com.acea.backend.dto.request;

import com.acea.backend.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Reset password request payload")
@PasswordMatch(password = "newPassword", confirmPassword = "confirmNewPassword")
public record ResetPasswordRequest(

        @NotBlank(message = "Reset token is required")
        @Schema(description = "The token received via reset email")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 128)
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#])[A-Za-z\\d@$!%*?&_#]{8,}$",
            message = "Password must contain uppercase, lowercase, digit, and special character"
        )
        @Schema(example = "NewSecurePass@456")
        String newPassword,

        @NotBlank(message = "Please confirm your new password")
        @Schema(example = "NewSecurePass@456")
        String confirmNewPassword
) {}
