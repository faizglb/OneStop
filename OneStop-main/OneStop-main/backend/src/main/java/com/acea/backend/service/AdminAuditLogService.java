package com.acea.backend.service;

import com.acea.backend.domain.enums.Role;
import com.acea.backend.dto.admin.AuditLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminAuditLogService {
    void logAction(UUID actorId, String actorEmail, Role actorRole, String action, String module, String targetId, String details);
    Page<AuditLogDto> getAuditLogs(String query, Pageable pageable);
}
