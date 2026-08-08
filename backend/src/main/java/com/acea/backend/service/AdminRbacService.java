package com.acea.backend.service;

import com.acea.backend.domain.enums.Role;
import com.acea.backend.dto.admin.RolePermissionDto;

import java.util.List;

public interface AdminRbacService {
    List<RolePermissionDto> getAllRolePermissions();
    RolePermissionDto getRolePermissions(Role role);
    void grantPermission(Role role, String permission);
    void revokePermission(Role role, String permission);
}
