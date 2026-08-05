package com.acea.backend.service;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.dto.skillgap.SkillGapAiRequest;
import com.acea.backend.dto.skillgap.SkillGapAiResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client that delegates skill gap analysis to the Python FastAPI AI service microservice.
 * Allows seamless integration of future deep learning models or LLMs without changing backend APIs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SkillGapAiClientService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public SkillGapAiResponse analyze(SkillGapAiRequest request) {
        String url = appProperties.aiServiceUrl() + "/api/analyze-skill-gap";
        log.info("Calling AI skill gap analysis service at: {} for student: {} and career: {}", 
                url, request.getStudentId(), request.getTargetCareer());

        try {
            ResponseEntity<SkillGapAiResponse> response =
                    restTemplate.postForEntity(url, request, SkillGapAiResponse.class);

            SkillGapAiResponse body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("AI service returned null response body.");
            }
            return body;

        } catch (Exception e) {
            log.error("Failed to call AI skill gap service: {}", e.getMessage(), e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The AI Skill Gap Analysis service is currently unavailable. Please try again later."
            );
        }
    }
}
