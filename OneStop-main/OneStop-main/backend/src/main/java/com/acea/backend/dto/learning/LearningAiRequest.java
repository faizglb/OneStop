package com.acea.backend.dto.learning;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record LearningAiRequest(
    // Student metrics
    String educationLevel,
    String currentCourse,
    BigDecimal currentCgpa,
    String category,
    String gender,
    List<String> careerGoals,
    Map<String, BigDecimal> aptitudeScores,
    Map<String, BigDecimal> personalityTraits,
    Map<String, BigDecimal> riasecProfile,
    List<String> recommendedCareers,
    List<String> recommendedColleges,
    List<String> recommendedScholarships,

    // Candidates
    List<ResourceProfileDto> resources
) {
    public record ResourceProfileDto(
        UUID resourceId,
        String title,
        String description,
        String platform,
        String resourceType,
        String category,
        String skill,
        String difficulty,
        String duration,
        BigDecimal rating,
        Boolean isFree
    ) {}
}
