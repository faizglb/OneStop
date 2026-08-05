package com.acea.backend.dto.skillgap;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapAiResponse {
    private String targetCareer;
    private BigDecimal overallReadinessScore;
    private BigDecimal overallGapPercentage;
    private Integer estimatedJobReadinessMonths;
    private ReadinessBreakdownDto readinessBreakdown;
    private List<SkillGapDto> skillGaps;
    private List<ImprovementRecommendationDto> improvementRecommendations;
    private VisualAnalyticsDto visualAnalytics;
    private List<SmartInsightDto> smartInsights;
}
