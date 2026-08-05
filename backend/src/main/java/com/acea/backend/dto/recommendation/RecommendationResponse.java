package com.acea.backend.dto.recommendation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
    UUID id,
    String careerName,
    String careerDescription,
    BigDecimal confidenceScore,
    BigDecimal matchScore,
    String reason,
    String expectedSalary,
    String industryGrowth,
    String requiredDegree,
    String futureScope,
    String jobDemand,
    String estimatedLearningTime,
    List<String> pros,
    List<String> challenges,
    Boolean isBookmarked,
    Instant createdAt,
    List<RecommendedSkillResponse> requiredSkills,
    List<RecommendedCertificationResponse> recommendedCertifications,
    List<RecommendedProjectResponse> suggestedProjects
) {
    public record RecommendedSkillResponse(
        UUID id,
        String skillName,
        String priority,
        BigDecimal confidence
    ) {}

    public record RecommendedCertificationResponse(
        UUID id,
        String certificationName,
        String provider,
        BigDecimal confidence
    ) {}

    public record RecommendedProjectResponse(
        UUID id,
        String projectTitle,
        String projectDescription,
        String difficulty,
        String estimatedDuration
    ) {}
}
