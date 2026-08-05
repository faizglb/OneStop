package com.acea.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    private String skillName;

    @NotBlank(message = "Skill level is required")
    @Size(max = 20, message = "Skill level cannot exceed 20 characters")
    private String skillLevel; // e.g. BEGINNER, INTERMEDIATE, ADVANCED

    @NotNull(message = "Experience in months is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceMonths;
}
