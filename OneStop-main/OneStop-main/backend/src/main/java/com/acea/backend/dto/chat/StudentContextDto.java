package com.acea.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentContextDto {
    private String studentName;
    private String careerGoal;
    private String branch;
    private String educationLevel;
    private Double currentCgpa;
    private String assessmentSummary;
    private String recommendedCareer;
    private String recommendedCollege;
    private String recommendedScholarship;
    private String roadmapSummary;
    private String skillGapSummary;
    private Double resumeScore;
    private String resumeSummary;
    private List<String> skills;
    private List<String> interests;
    private String familyIncome;
    private String category;
}
