package com.acea.backend.dto.skillgap;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapReportResponse {
    private UUID id;
    private UUID studentId;
    private String careerId;
    private String targetCareer;
    private BigDecimal overallReadinessScore;
    private BigDecimal overallGapPercentage;
    private Integer estimatedJobReadinessMonths;
    private ReadinessBreakdownDto readinessBreakdown;
    private List<SkillGapDto> skillGaps;
    private List<ImprovementRecommendationDto> improvementRecommendations;
    private VisualAnalyticsDto visualAnalytics;
    private List<SmartInsightDto> smartInsights;
    private Instant createdAt;
    private Instant updatedAt;
}
