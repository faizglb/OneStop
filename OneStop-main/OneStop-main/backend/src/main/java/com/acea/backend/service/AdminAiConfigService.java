package com.acea.backend.service;

import com.acea.backend.dto.admin.AdminAiConfigDto;

import java.util.UUID;

public interface AdminAiConfigService {
    AdminAiConfigDto getAiConfig();
    AdminAiConfigDto updateAiConfig(AdminAiConfigDto dto, UUID adminId);
}
