package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.AIConfiguration;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.AIConfigurationRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.admin.AdminAiConfigDto;
import com.acea.backend.service.AdminAiConfigService;
import com.acea.backend.service.AdminAuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminAiConfigServiceImpl implements AdminAiConfigService {

    private final AIConfigurationRepository aiConfigRepository;
    private final UserRepository userRepository;
    private final AdminAuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public AdminAiConfigDto getAiConfig() {
        String provider = getConfigValue("llm_provider", "GEMINI");
        String geminiModel = getConfigValue("gemini_model", "gemini-1.5-pro");
        String openAiModel = getConfigValue("openai_model", "gpt-4o");
        double temp = Double.parseDouble(getConfigValue("temperature", "0.7"));
        double topP = Double.parseDouble(getConfigValue("top_p", "0.95"));
        int maxTokens = Integer.parseInt(getConfigValue("max_tokens", "2048"));
        String embModel = getConfigValue("embedding_model", "text-embedding-004");
        double threshold = Double.parseDouble(getConfigValue("similarity_threshold", "0.75"));
        String chunking = getConfigValue("chunking_strategy", "SEMANTIC_PARAGRAPH");

        return AdminAiConfigDto.builder()
            .llmProvider(provider)
            .geminiModel(geminiModel)
            .openAiModel(openAiModel)
            .temperature(temp)
            .topP(topP)
            .maxTokens(maxTokens)
            .embeddingModel(embModel)
            .similarityThreshold(threshold)
            .chunkingStrategy(chunking)
            .build();
    }

    @Override
    public AdminAiConfigDto updateAiConfig(AdminAiConfigDto dto, UUID adminId) {
        saveConfigValue("llm_provider", dto.getLlmProvider(), adminId);
        if (dto.getGeminiModel() != null) saveConfigValue("gemini_model", dto.getGeminiModel(), adminId);
        if (dto.getOpenAiModel() != null) saveConfigValue("openai_model", dto.getOpenAiModel(), adminId);
        saveConfigValue("temperature", String.valueOf(dto.getTemperature()), adminId);
        saveConfigValue("top_p", String.valueOf(dto.getTopP()), adminId);
        saveConfigValue("max_tokens", String.valueOf(dto.getMaxTokens()), adminId);
        if (dto.getEmbeddingModel() != null) saveConfigValue("embedding_model", dto.getEmbeddingModel(), adminId);
        saveConfigValue("similarity_threshold", String.valueOf(dto.getSimilarityThreshold()), adminId);
        if (dto.getChunkingStrategy() != null) saveConfigValue("chunking_strategy", dto.getChunkingStrategy(), adminId);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.SUPER_ADMIN,
            "UPDATE_AI_CONFIG",
            "AI_CONFIGURATION",
            "GLOBAL",
            "Updated LLM provider to " + dto.getLlmProvider() + ", temp: " + dto.getTemperature()
        );

        return getAiConfig();
    }

    private String getConfigValue(String key, String defaultValue) {
        return aiConfigRepository.findByConfigKey(key)
            .map(AIConfiguration::getConfigValue)
            .orElse(defaultValue);
    }

    private void saveConfigValue(String key, String value, UUID adminId) {
        AIConfiguration config = aiConfigRepository.findByConfigKey(key)
            .orElse(AIConfiguration.builder()
                .configKey(key)
                .description("System configuration setting for " + key)
                .build());
        config.setConfigValue(value);
        config.setUpdatedBy(adminId);
        aiConfigRepository.save(config);
    }
}
