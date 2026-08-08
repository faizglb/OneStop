package com.acea.backend.controller.admin;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.admin.AdminAiConfigDto;
import com.acea.backend.service.AdminAiConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/ai/config")
@RequiredArgsConstructor
@Tag(name = "Admin - AI Configuration", description = "Endpoints for configuring LLM provider, models, temperature, and RAG thresholds")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminAiConfigController {

    private final AdminAiConfigService adminAiConfigService;

    @GetMapping
    @Operation(summary = "Get current AI engine configuration")
    public ResponseEntity<AdminAiConfigDto> getAiConfig() {
        return ResponseEntity.ok(adminAiConfigService.getAiConfig());
    }

    @PutMapping
    @Operation(summary = "Update AI engine configuration")
    public ResponseEntity<AdminAiConfigDto> updateAiConfig(
        @RequestBody AdminAiConfigDto dto,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        return ResponseEntity.ok(adminAiConfigService.updateAiConfig(dto, adminId));
    }
}
