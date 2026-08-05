package com.acea.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionResponse {
    private UUID id;
    private String title;
    private String status;
    private OffsetDateTime startedAt;
    private OffsetDateTime lastMessageAt;
    private Integer messageCount;
    private String lastMessagePreview;
}
