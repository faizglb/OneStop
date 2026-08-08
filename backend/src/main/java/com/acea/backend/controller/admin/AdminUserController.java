package com.acea.backend.controller.admin;

import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.dto.admin.AdminUserResponseDto;
import com.acea.backend.dto.admin.AdminUserUpdateDto;
import com.acea.backend.service.AdminUserService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Endpoints for user management, role assignment, and account status")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Get paginated user list with optional search and filters")
    public ResponseEntity<Page<AdminUserResponseDto>> getUsers(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Role role,
        @RequestParam(required = false) Boolean suspended,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminUserService.getUsers(
            search, role, suspended, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user details by ID")
    public ResponseEntity<AdminUserResponseDto> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user profile, role, or tier")
    public ResponseEntity<AdminUserResponseDto> updateUser(
        @PathVariable UUID userId,
        @RequestBody AdminUserUpdateDto dto,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        return ResponseEntity.ok(adminUserService.updateUser(userId, dto, adminId));
    }

    @PostMapping("/{userId}/suspend")
    @Operation(summary = "Suspend a user account with a reason")
    public ResponseEntity<AdminUserResponseDto> suspendUser(
        @PathVariable UUID userId,
        @RequestBody Map<String, String> body,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        String reason = body.getOrDefault("reason", "Suspended by Admin");
        return ResponseEntity.ok(adminUserService.suspendUser(userId, reason, adminId));
    }

    @PostMapping("/{userId}/activate")
    @Operation(summary = "Reactivate a suspended user account")
    public ResponseEntity<AdminUserResponseDto> activateUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        return ResponseEntity.ok(adminUserService.activateUser(userId, adminId));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete (soft delete) a user account (Super Admin only)")
    public ResponseEntity<Void> deleteUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        adminUserService.deleteUser(userId, adminId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "Reset a user's password")
    public ResponseEntity<Void> resetPassword(
        @PathVariable UUID userId,
        @RequestBody Map<String, String> body,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().build();
        }
        adminUserService.resetPassword(userId, newPassword, adminId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/assign-role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Assign a role to a user (Super Admin only)")
    public ResponseEntity<AdminUserResponseDto> assignRole(
        @PathVariable UUID userId,
        @RequestParam Role role,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        return ResponseEntity.ok(adminUserService.assignRole(userId, role, adminId));
    }

    @GetMapping("/export")
    @Operation(summary = "Export all user data as CSV/JSON payload")
    public ResponseEntity<List<AdminUserResponseDto>> exportUsers() {
        return ResponseEntity.ok(adminUserService.exportUsers());
    }
}
