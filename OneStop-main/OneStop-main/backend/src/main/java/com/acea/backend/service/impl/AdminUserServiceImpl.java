package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.admin.AdminUserResponseDto;
import com.acea.backend.dto.admin.AdminUserUpdateDto;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.AdminAuditLogService;
import com.acea.backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponseDto> getUsers(String search, Role role, Boolean suspended, Pageable pageable) {
        Page<User> page;
        if (search != null && !search.isBlank()) {
            page = userRepository.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                search, search, search, pageable
            );
        } else if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return page.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToDto(user);
    }

    @Override
    public AdminUserResponseDto updateUser(UUID userId, AdminUserUpdateDto dto, UUID adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getAccountTier() != null) user.setAccountTier(dto.getAccountTier());
        if (dto.getActive() != null) user.setActive(dto.getActive());
        if (dto.getSuspended() != null) {
            user.setSuspended(dto.getSuspended());
            user.setSuspensionReason(dto.getSuspensionReason());
        }

        User saved = userRepository.save(user);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "UPDATE_USER",
            "USER_MANAGEMENT",
            userId.toString(),
            "Updated user profile & role to " + saved.getRole()
        );

        return mapToDto(saved);
    }

    @Override
    public AdminUserResponseDto suspendUser(UUID userId, String reason, UUID adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setSuspended(true);
        user.setSuspensionReason(reason != null ? reason : "Suspended by Admin");
        User saved = userRepository.save(user);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "SUSPEND_USER",
            "USER_MANAGEMENT",
            userId.toString(),
            "Suspended account: " + reason
        );

        return mapToDto(saved);
    }

    @Override
    public AdminUserResponseDto activateUser(UUID userId, UUID adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setSuspended(false);
        user.setSuspensionReason(null);
        user.setActive(true);
        User saved = userRepository.save(user);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "ACTIVATE_USER",
            "USER_MANAGEMENT",
            userId.toString(),
            "Reactivated account"
        );

        return mapToDto(saved);
    }

    @Override
    public void deleteUser(UUID userId, UUID adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.softDelete();
        userRepository.save(user);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "DELETE_USER",
            "USER_MANAGEMENT",
            userId.toString(),
            "Soft deleted user " + user.getEmail()
        );
    }

    @Override
    public void resetPassword(UUID userId, String newPassword, UUID adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "RESET_PASSWORD",
            "USER_MANAGEMENT",
            userId.toString(),
            "Admin reset user password"
        );
    }

    @Override
    public AdminUserResponseDto assignRole(UUID userId, Role role, UUID adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(role);
        User saved = userRepository.save(user);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "ASSIGN_ROLE",
            "USER_MANAGEMENT",
            userId.toString(),
            "Assigned role: " + role.name()
        );

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponseDto> exportUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    private AdminUserResponseDto mapToDto(User user) {
        return AdminUserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .avatarUrl(user.getAvatarUrl())
            .role(user.getRole())
            .accountTier(user.getAccountTier())
            .active(user.isActive())
            .suspended(user.isSuspended())
            .suspensionReason(user.getSuspensionReason())
            .emailVerified(user.isEmailVerified())
            .lastLoginAt(user.getLastLoginAt())
            .loginCount(user.getLoginCount())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
