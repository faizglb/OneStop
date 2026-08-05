package com.acea.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private UUID id;
    private UUID sessionId;
    private String sender;
    private String message;
    private String messageType;
    private String agentUsed;
    private Double confidenceScore;
    private List<SourceCitationDto> sources;
    private OffsetDateTime timestamp;
}
