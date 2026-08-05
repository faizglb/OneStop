package com.acea.backend.dto.scholarship;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScholarshipApplicationHistoryResponse(
    UUID id,
    UUID scholarshipId,
    String scholarshipName,
    String provider,
    BigDecimal amount,
    LocalDate lastDate,
    String status, // BOOKMARKED | APPLIED | SHORTLISTED | COMPLETED
    String notes,
    Instant reminderDate,
    Instant updatedAt
) {}
