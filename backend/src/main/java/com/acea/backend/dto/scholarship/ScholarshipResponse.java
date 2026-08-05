package com.acea.backend.dto.scholarship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ScholarshipResponse(
    UUID id,
    String scholarshipName,
    String provider,
    String scholarshipType,
    String description,
    String officialWebsite,
    String applicationLink,
    String applicationMode,
    BigDecimal amount,
    String currency,
    LocalDate lastDate,
    String state,
    String country,
    String educationLevel,
    String course,
    String category,
    String gender,
    Boolean minorityEligible,
    Boolean disabilityEligible,
    BigDecimal incomeLimit,
    BigDecimal minimumMarks,
    String entranceExam,
    Boolean renewable,
    String requiredDocuments,
    String status,
    Boolean isBookmarked,
    String applicationStatus, // null or tracker status (BOOKMARKED, APPLIED, SHORTLISTED, COMPLETED)
    List<EligibilityDto> eligibilities
) {
    public record EligibilityDto(
        UUID id,
        String eligibilityRule,
        String ruleType,
        String ruleValue
    ) {}
}
