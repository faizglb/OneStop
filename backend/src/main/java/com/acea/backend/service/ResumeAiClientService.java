package com.acea.backend.service;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.dto.resume.ResumeAiRequest;
import com.acea.backend.dto.resume.ResumeAiResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client that delegates resume analysis to the Python FastAPI AI service.
 * Isolated here so the AI backend can be swapped (e.g., LLM-based) without
 * touching any Spring service business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeAiClientService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public ResumeAiResponse analyze(ResumeAiRequest request) {
        String url = appProperties.aiServiceUrl() + "/api/analyze-resume";
        log.info("Calling AI resume analysis service at: {}", url);

        try {
            ResponseEntity<ResumeAiResponse> response =
                    restTemplate.postForEntity(url, request, ResumeAiResponse.class);

            ResumeAiResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("AI service returned null response body.");
            }
            return body;

        } catch (Exception e) {
            log.error("Failed to call AI resume analysis service: {}", e.getMessage(), e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The AI Resume Analyzer service is currently offline. Please try again later."
            );
        }
    }
}
