package com.acea.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAiConfigDto {
    private String llmProvider; // GEMINI | OPENAI
    private String geminiModel;
    private String openAiModel;
    private double temperature;
    private double topP;
    private int maxTokens;
    private String embeddingModel;
    private double similarityThreshold;
    private String chunkingStrategy;
}
