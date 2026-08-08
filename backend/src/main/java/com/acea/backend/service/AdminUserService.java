package com.acea.backend.service;

import com.acea.backend.domain.enums.Role;
import com.acea.backend.dto.admin.AdminUserResponseDto;
import com.acea.backend.dto.admin.AdminUserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {
    Page<AdminUserResponseDto> getUsers(String search, Role role, Boolean suspended, Pageable pageable);
    AdminUserResponseDto getUserById(UUID userId);
    AdminUserResponseDto updateUser(UUID userId, AdminUserUpdateDto dto, UUID adminId);
    AdminUserResponseDto suspendUser(UUID userId, String reason, UUID adminId);
    AdminUserResponseDto activateUser(UUID userId, UUID adminId);
    void deleteUser(UUID userId, UUID adminId);
    void resetPassword(UUID userId, String newPassword, UUID adminId);
    AdminUserResponseDto assignRole(UUID userId, Role role, UUID adminId);
    List<AdminUserResponseDto> exportUsers();
}
