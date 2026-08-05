package com.acea.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceCitationDto {
    private String documentTitle;
    private String sourceType;
    private String chunkText;
    private Double relevanceScore;
    private String knowledgeSource;
}
