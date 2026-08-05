package com.acea.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalRequest {

    @NotBlank(message = "Goal title is required")
    @Size(max = 200, message = "Goal title cannot exceed 200 characters")
    private String goalTitle;

    @NotBlank(message = "Goal description is required")
    private String goalDescription;

    @NotNull(message = "Target year is required")
    @Min(value = 2026, message = "Target year must be in the future")
    private Integer targetYear;

    @NotBlank(message = "Priority is required")
    @Size(max = 20, message = "Priority cannot exceed 20 characters")
    private String priority; // HIGH, MEDIUM, LOW
}
