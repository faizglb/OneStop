package com.acea.backend.dto.recommendation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Request payload sent to FastAPI Python Service. */
public record AiServiceRequest(
    String educationLevel,
    String currentCourse,
    String branch,
    Integer currentYear,
    BigDecimal currentCgpa,
    List<String> currentSkills,
    List<String> interests,
    List<String> careerGoals,
    List<String> languagesKnown,

    // Assessments
    Map<String, BigDecimal> aptitudeScores,       // categoryName -> percentage
    Map<String, BigDecimal> personalityTraits,     // traitKey -> normalizedScore
    Map<String, BigDecimal> riasecProfile          // categoryKey -> normalizedScore
) {}
