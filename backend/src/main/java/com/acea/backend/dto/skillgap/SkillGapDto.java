package com.acea.backend.dto.skillgap;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapDto {
    private UUID id;
    private String skillName;
    private String requiredLevel;
    private String currentLevel;
    private BigDecimal gapPercentage;
    private String priority;
    private Integer estimatedLearningHours;
    private String status;
    private String category;
    private List<ImprovementRecommendationDto> recommendations;
}
