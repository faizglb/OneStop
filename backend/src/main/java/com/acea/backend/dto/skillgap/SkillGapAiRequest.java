package com.acea.backend.dto.skillgap;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapAiRequest {
    private String studentId;
    private String targetCareer;
    private String careerId;
    private List<String> currentSkills;
    private List<String> profileProjects;
    private List<String> profileCertifications;
    private List<String> resumeKeywords;
    private BigDecimal resumeAtsScore;
    private Map<String, BigDecimal> assessmentAptitudeScores;
    private Integer roadmapCompletionPct;
}
