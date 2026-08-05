package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.Notification;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.NotificationRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.admin.NotificationDto;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.AdminAuditLogService;
import com.acea.backend.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AdminAuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public NotificationDto createNotification(NotificationDto dto, UUID adminId) {
        Notification notification = Notification.builder()
            .title(dto.getTitle())
            .message(dto.getMessage())
            .type(dto.getType() != null ? dto.getType() : "ANNOUNCEMENT")
            .targetRole(dto.getTargetRole() != null ? dto.getTargetRole() : "ALL")
            .priority(dto.getPriority() != null ? dto.getPriority() : "NORMAL")
            .active(true)
            .expiresAt(dto.getExpiresAt())
            .build();

        Notification saved = notificationRepository.save(notification);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "CREATE_NOTIFICATION",
            "NOTIFICATION_MANAGEMENT",
            saved.getId().toString(),
            "Broadcast announcement: " + dto.getTitle()
        );

        return mapToDto(saved);
    }

    @Override
    public void deleteNotification(UUID notificationId, UUID adminId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        notificationRepository.delete(notification);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "DELETE_NOTIFICATION",
            "NOTIFICATION_MANAGEMENT",
            notificationId.toString(),
            "Deleted notification " + notification.getTitle()
        );
    }

    private NotificationDto mapToDto(Notification n) {
        return NotificationDto.builder()
            .id(n.getId())
            .title(n.getTitle())
            .message(n.getMessage())
            .type(n.getType())
            .targetRole(n.getTargetRole())
            .priority(n.getPriority())
            .active(n.isActive())
            .expiresAt(n.getExpiresAt())
            .createdAt(n.getCreatedAt())
            .build();
    }
}
