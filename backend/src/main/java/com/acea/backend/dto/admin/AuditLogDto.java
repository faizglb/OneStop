package com.acea.backend.dto.admin;

import com.acea.backend.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private UUID id;
    private UUID actorId;
    private String actorEmail;
    private Role actorRole;
    private String action;
    private String module;
    private String targetId;
    private String details;
    private String ipAddress;
    private Instant createdAt;
}
