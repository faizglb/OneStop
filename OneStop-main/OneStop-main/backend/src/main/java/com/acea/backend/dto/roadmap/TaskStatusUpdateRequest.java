package com.acea.backend.dto.roadmap;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record TaskStatusUpdateRequest(
    @NotBlank String status, // PENDING | IN_PROGRESS | COMPLETED | SKIPPED
    LocalDate dueDate
) {}
