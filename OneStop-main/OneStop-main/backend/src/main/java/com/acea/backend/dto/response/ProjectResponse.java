package com.acea.backend.dto.response;

import java.util.List;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String projectTitle,
    String description,
    List<String> techStack,
    String githubLink,
    String liveLink
) {}
