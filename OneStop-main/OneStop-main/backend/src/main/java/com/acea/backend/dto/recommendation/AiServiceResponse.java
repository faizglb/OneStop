package com.acea.backend.dto.recommendation;

import java.math.BigDecimal;
import java.util.List;

public record AiServiceResponse(
    List<CareerRecommendationDto> recommendations
) {
    public record CareerRecommendationDto(
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
        List<RecommendedSkillDto> requiredSkills,
        List<RecommendedCertificationDto> recommendedCertifications,
        List<RecommendedProjectDto> suggestedProjects
    ) {}

    public record RecommendedSkillDto(
        String skillName,
        String priority, // HIGH | MEDIUM | LOW
        BigDecimal confidence
    ) {}

    public record RecommendedCertificationDto(
        String certificationName,
        String provider,
        BigDecimal confidence
    ) {}

    public record RecommendedProjectDto(
        String projectTitle,
        String projectDescription,
        String difficulty, // BEGINNER | INTERMEDIATE | ADVANCED
        String estimatedDuration
    ) {}
}
