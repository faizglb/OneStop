package com.acea.backend.dto.skillgap;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillGapAnalyzeRequest {

    @NotBlank(message = "Target career is required")
    private String targetCareer;

    private String careerId;
}
