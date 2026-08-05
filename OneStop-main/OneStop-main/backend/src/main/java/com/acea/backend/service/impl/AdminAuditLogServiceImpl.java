package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.AdminLog;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.AdminLogRepository;
import com.acea.backend.dto.admin.AuditLogDto;
import com.acea.backend.service.AdminAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAuditLogServiceImpl implements AdminAuditLogService {

    private final AdminLogRepository adminLogRepository;

    @Override
    public void logAction(UUID actorId, String actorEmail, Role actorRole, String action, String module, String targetId, String details) {
        AdminLog log = AdminLog.builder()
            .actorId(actorId != null ? actorId : UUID.randomUUID())
            .actorEmail(actorEmail != null ? actorEmail : "system@acea.com")
            .actorRole(actorRole != null ? actorRole : Role.SUPER_ADMIN)
            .action(action)
            .module(module)
            .targetId(targetId)
            .details(details)
            .ipAddress("127.0.0.1")
            .build();
        adminLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAuditLogs(String query, Pageable pageable) {
        Page<AdminLog> logs;
        if (query != null && !query.isBlank()) {
            logs = adminLogRepository.findByModuleContainingIgnoreCaseOrActionContainingIgnoreCaseOrActorEmailContainingIgnoreCase(
                query, query, query, pageable
            );
        } else {
            logs = adminLogRepository.findAll(pageable);
        }

        return logs.map(log -> AuditLogDto.builder()
            .id(log.getId())
            .actorId(log.getActorId())
            .actorEmail(log.getActorEmail())
            .actorRole(log.getActorRole())
            .action(log.getAction())
            .module(log.getModule())
            .targetId(log.getTargetId())
            .details(log.getDetails())
            .ipAddress(log.getIpAddress())
            .createdAt(log.getCreatedAt())
            .build()
        );
    }
}
