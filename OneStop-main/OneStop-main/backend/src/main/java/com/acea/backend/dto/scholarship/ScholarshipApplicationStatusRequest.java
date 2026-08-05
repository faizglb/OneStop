package com.acea.backend.dto.scholarship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ScholarshipApplicationStatusRequest(
    @NotNull UUID scholarshipId,
    @NotBlank String status, // BOOKMARKED | APPLIED | SHORTLISTED | COMPLETED
    String notes,
    Instant reminderDate
) {}
