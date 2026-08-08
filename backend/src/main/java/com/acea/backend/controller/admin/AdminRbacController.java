package com.acea.backend.controller.admin;

import com.acea.backend.domain.enums.Role;
import com.acea.backend.dto.admin.RolePermissionDto;
import com.acea.backend.service.AdminRbacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@Tag(name = "Admin - RBAC Management", description = "Endpoints for managing roles and granular permissions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminRbacController {

    private final AdminRbacService adminRbacService;

    @GetMapping
    @Operation(summary = "Get all admin roles and their permissions")
    public ResponseEntity<List<RolePermissionDto>> getAllRolePermissions() {
        return ResponseEntity.ok(adminRbacService.getAllRolePermissions());
    }

    @GetMapping("/{role}")
    @Operation(summary = "Get permissions for a specific role")
    public ResponseEntity<RolePermissionDto> getRolePermissions(@PathVariable Role role) {
        return ResponseEntity.ok(adminRbacService.getRolePermissions(role));
    }

    @PostMapping("/{role}/permissions/grant")
    @Operation(summary = "Grant a permission to a role")
    public ResponseEntity<Void> grantPermission(
        @PathVariable Role role,
        @RequestParam String permission
    ) {
        adminRbacService.grantPermission(role, permission);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{role}/permissions/revoke")
    @Operation(summary = "Revoke a permission from a role")
    public ResponseEntity<Void> revokePermission(
        @PathVariable Role role,
        @RequestParam String permission
    ) {
        adminRbacService.revokePermission(role, permission);
        return ResponseEntity.ok().build();
    }
}
