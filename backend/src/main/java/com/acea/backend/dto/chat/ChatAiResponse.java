package com.acea.backend.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAiResponse {
    private String response;

    @JsonProperty("agent_used")
    private String agentUsed;

    private List<SourceCitationDto> sources;

    @JsonProperty("confidence_score")
    private Double confidenceScore;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("is_personalized")
    private Boolean isPersonalized;
}
