package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.learning.LearningRecommendationResponse;
import com.acea.backend.service.LearningRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/learning-recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Recommendations", description = "AI Learning Resources Recommendation Engine")
public class LearningRecommendationController {

    private final LearningRecommendationService recommendationService;

    @PostMapping("/generate")
    @Operation(summary = "Generate AI educational recommendations aligned with career gap plans and assessment checks")
    public ResponseEntity<List<LearningRecommendationResponse>> generateRecommendations(
            @AuthenticationPrincipal User user) {
        log.info("Generating learning recommendations for user={}", user.getId());
        return ResponseEntity.ok(recommendationService.generateRecommendations(user.getId()));
    }

    @GetMapping
    @Operation(summary = "Get all educational suggestions for the student")
    public ResponseEntity<List<LearningRecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId()));
    }
}
