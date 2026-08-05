package com.acea.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRequest {

    @NotBlank(message = "Certificate name is required")
    @Size(max = 200, message = "Certificate name cannot exceed 200 characters")
    private String certificateName;

    @NotBlank(message = "Platform is required")
    @Size(max = 100, message = "Platform cannot exceed 100 characters")
    private String platform;

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date must be in the past or present")
    private LocalDate issueDate;

    @NotBlank(message = "Certificate URL is required")
    @Size(max = 500, message = "Certificate URL cannot exceed 500 characters")
    @URL(message = "Invalid certificate URL")
    private String certificateUrl;
}
