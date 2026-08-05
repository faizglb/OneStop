package com.acea.backend.dto.admin;

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
public class NotificationDto {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String targetRole;
    private String priority;
    private boolean active;
    private Instant expiresAt;
    private Instant createdAt;
}
