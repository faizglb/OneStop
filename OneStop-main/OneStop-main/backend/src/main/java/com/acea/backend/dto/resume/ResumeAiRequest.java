package com.acea.backend.dto.resume;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Payload sent to the Python FastAPI AI service for resume analysis.
 */
public record ResumeAiRequest(

        @JsonProperty("resumeText")
        String resumeText,

        @JsonProperty("fileName")
        String fileName,

        @JsonProperty("targetCareer")
        String targetCareer,

        // Student profile context (from Sprint 4 career recommendations)
        @JsonProperty("currentSkills")
        List<String> currentSkills,

        @JsonProperty("careerGoals")
        List<String> careerGoals,

        @JsonProperty("branch")
        String branch,

        @JsonProperty("educationLevel")
        String educationLevel
) {}
