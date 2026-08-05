package com.acea.backend.dto.scholarship;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ScholarshipAiRequest(
    // Student metrics
    String educationLevel,
    String currentCourse,
    BigDecimal currentCgpa,
    BigDecimal familyIncome,
    String category,
    String gender,
    String state,
    Boolean isMinority,
    Boolean hasDisability,
    List<String> careerGoals,
    Map<String, BigDecimal> aptitudeScores,
    Map<String, BigDecimal> personalityTraits,
    Map<String, BigDecimal> riasecProfile,
    List<String> recommendedCareers,
    List<String> recommendedColleges,

    // Candidates
    List<ScholarshipProfileDto> scholarships
) {
    public record ScholarshipProfileDto(
        UUID scholarshipId,
        String scholarshipName,
        String provider,
        String scholarshipType,
        BigDecimal amount,
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
        Boolean renewable,
        String description,
        List<EligibilityRuleDto> rules
    ) {}

    public record EligibilityRuleDto(
        String ruleType,
        String ruleValue,
        String eligibilityRule
    ) {}
}
