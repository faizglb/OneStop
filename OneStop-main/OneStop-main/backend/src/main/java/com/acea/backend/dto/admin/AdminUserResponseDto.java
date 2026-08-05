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
public class AdminUserResponseDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
    private Role role;
    private String accountTier;
    private boolean active;
    private boolean suspended;
    private String suspensionReason;
    private boolean emailVerified;
    private Instant lastLoginAt;
    private int loginCount;
    private Instant createdAt;
    private Instant updatedAt;
}
