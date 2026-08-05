package com.acea.backend.service;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.dto.chat.ChatAiRequest;
import com.acea.backend.dto.chat.ChatAiResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Client service that delegates AI Chatbot queries to the Python FastAPI AI microservice.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAiClientService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public ChatAiResponse chat(ChatAiRequest request) {
        String url = appProperties.aiServiceUrl() + "/api/chat";
        log.info("Calling AI Chatbot service at: {} for session: {}", url, request.getSessionId());

        try {
            ResponseEntity<ChatAiResponse> response =
                    restTemplate.postForEntity(url, request, ChatAiResponse.class);

            ChatAiResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("AI Chatbot service returned null response body.");
            }
            return body;

        } catch (Exception e) {
            log.error("Failed to call AI Chatbot service: {}", e.getMessage(), e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The AI Career Advisor Chatbot is currently unavailable. Please try again later."
            );
        }
    }
}
