package com.acea.backend.dto.response;

import java.util.UUID;

public record GoalResponse(
    UUID id,
    String goalTitle,
    String goalDescription,
    Integer targetYear,
    String priority
) {}
