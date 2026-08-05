package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.RolePermission;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.RolePermissionRepository;
import com.acea.backend.dto.admin.RolePermissionDto;
import com.acea.backend.service.AdminRbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRbacServiceImpl implements AdminRbacService {

    private final RolePermissionRepository rolePermissionRepository;

    private static final Map<Role, List<String>> DEFAULT_ROLE_PERMISSIONS = Map.of(
        Role.SUPER_ADMIN, List.of("USERS_MANAGE", "ROLES_MANAGE", "CONTENT_MANAGE", "AI_CONFIG_MANAGE", "KNOWLEDGE_BASE_MANAGE", "ANALYTICS_VIEW", "SYSTEM_HEALTH"),
        Role.ADMIN, List.of("USERS_MANAGE", "CONTENT_MANAGE", "KNOWLEDGE_BASE_MANAGE", "ANALYTICS_VIEW", "SYSTEM_HEALTH"),
        Role.CONTENT_MANAGER, List.of("CONTENT_MANAGE", "KNOWLEDGE_BASE_READ", "ANALYTICS_VIEW")
    );

    @Override
    @Transactional(readOnly = true)
    public List<RolePermissionDto> getAllRolePermissions() {
        return Arrays.stream(Role.values())
            .filter(Role::isAdminLevel)
            .map(this::getRolePermissions)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RolePermissionDto getRolePermissions(Role role) {
        List<RolePermission> permissions = rolePermissionRepository.findByRole(role);
        List<String> permList = permissions.stream()
            .map(RolePermission::getPermission)
            .collect(Collectors.toList());

        if (permList.isEmpty() && DEFAULT_ROLE_PERMISSIONS.containsKey(role)) {
            permList = DEFAULT_ROLE_PERMISSIONS.get(role);
        }

        return RolePermissionDto.builder()
            .role(role)
            .roleName(role.name())
            .description(role.getDescription())
            .permissions(permList)
            .build();
    }

    @Override
    public void grantPermission(Role role, String permission) {
        if (rolePermissionRepository.findByRoleAndPermission(role, permission).isEmpty()) {
            RolePermission rp = RolePermission.builder()
                .role(role)
                .permission(permission)
                .description("Permission granted to " + role.name())
                .build();
            rolePermissionRepository.save(rp);
        }
    }

    @Override
    public void revokePermission(Role role, String permission) {
        rolePermissionRepository.deleteByRoleAndPermission(role, permission);
    }
}
