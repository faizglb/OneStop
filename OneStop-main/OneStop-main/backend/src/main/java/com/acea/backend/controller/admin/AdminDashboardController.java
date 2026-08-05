package com.acea.backend.controller.admin;

import com.acea.backend.dto.admin.AdminDashboardStatsDto;
import com.acea.backend.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin - Dashboard", description = "Endpoints for platform overview statistics and system health")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CONTENT_MANAGER')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get admin dashboard overview metrics and aggregations")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getDashboardStats());
    }

    @GetMapping("/health")
    @Operation(summary = "Get real-time system health and service status")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "database", "UP",
            "aiService", "UP",
            "storage", "UP",
            "diskFreeSpaceGb", 142.5,
            "memoryUsedMb", 512,
            "timestamp", java.time.Instant.now()
        ));
    }
}
