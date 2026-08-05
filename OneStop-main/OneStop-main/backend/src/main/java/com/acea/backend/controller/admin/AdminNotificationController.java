package com.acea.backend.controller.admin;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.admin.NotificationDto;
import com.acea.backend.service.AdminNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Admin - Notifications", description = "Endpoints for platform announcements and maintenance notifications")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminNotificationController {

    private final AdminNotificationService adminNotificationService;

    @GetMapping
    @Operation(summary = "Get list of notifications")
    public ResponseEntity<Page<NotificationDto>> getNotifications(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminNotificationService.getNotifications(
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }

    @PostMapping
    @Operation(summary = "Create and broadcast platform announcement or alert")
    public ResponseEntity<NotificationDto> createNotification(
        @RequestBody NotificationDto dto,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        return ResponseEntity.ok(adminNotificationService.createNotification(dto, adminId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification")
    public ResponseEntity<Void> deleteNotification(
        @PathVariable UUID id,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        adminNotificationService.deleteNotification(id, adminId);
        return ResponseEntity.noContent().build();
    }
}
