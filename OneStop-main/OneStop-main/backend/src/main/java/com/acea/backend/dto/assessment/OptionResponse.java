package com.acea.backend.dto.assessment;

import java.util.UUID;

/** Single answer option — isCorrect and weight are NEVER included. */
public record OptionResponse(
    UUID id,
    String optionText,
    int optionOrder
) {}
