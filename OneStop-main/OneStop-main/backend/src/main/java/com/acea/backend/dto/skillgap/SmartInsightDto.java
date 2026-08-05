package com.acea.backend.dto.skillgap;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmartInsightDto {
    private String title;
    private String description;
    private String insightType;
}
