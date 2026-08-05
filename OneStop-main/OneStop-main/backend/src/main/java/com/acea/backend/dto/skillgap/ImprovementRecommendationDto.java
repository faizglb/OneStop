package com.acea.backend.dto.skillgap;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImprovementRecommendationDto {
    private UUID id;
    private String resourceId;
    private String projectId;
    private String certificateId;
    private String title;
    private String description;
    private String recommendationType;
    private String priority;
    private String estimatedDuration;
    private String url;
}
