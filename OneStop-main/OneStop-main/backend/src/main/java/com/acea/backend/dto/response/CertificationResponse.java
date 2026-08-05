package com.acea.backend.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record CertificationResponse(
    UUID id,
    String certificateName,
    String platform,
    LocalDate issueDate,
    String certificateUrl
) {}
