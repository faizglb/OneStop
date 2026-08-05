package com.acea.backend.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAiRequest {
    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("student_id")
    private String studentId;

    private String message;

    @JsonProperty("student_context")
    private StudentContextDto studentContext;
}
