package com.acea.backend.service;

import com.acea.backend.dto.admin.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminNotificationService {
    Page<NotificationDto> getNotifications(Pageable pageable);
    NotificationDto createNotification(NotificationDto dto, UUID adminId);
    void deleteNotification(UUID notificationId, UUID adminId);
}
