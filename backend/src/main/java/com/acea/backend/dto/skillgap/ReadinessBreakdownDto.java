package com.acea.backend.dto.skillgap;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadinessBreakdownDto {
    private BigDecimal overallReadinessScore;
    private BigDecimal technicalReadiness;
    private BigDecimal softSkillReadiness;
    private BigDecimal projectReadiness;
    private BigDecimal interviewReadiness;
    private BigDecimal resumeReadiness;
    private BigDecimal codingReadiness;
}
