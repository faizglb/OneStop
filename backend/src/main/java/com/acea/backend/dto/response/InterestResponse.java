package com.acea.backend.dto.response;

import java.util.UUID;

public record InterestResponse(
    UUID id,
    String interestName
) {}
