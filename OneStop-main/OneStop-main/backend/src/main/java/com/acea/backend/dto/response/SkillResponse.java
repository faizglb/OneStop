package com.acea.backend.dto.response;

import java.util.UUID;

public record SkillResponse(
    UUID id,
    String skillName,
    String skillLevel,
    Integer experienceMonths
) {}
