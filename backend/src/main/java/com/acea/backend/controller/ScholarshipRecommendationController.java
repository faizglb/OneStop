package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.scholarship.ScholarshipRecommendationResponse;
import com.acea.backend.service.ScholarshipRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scholarship-recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Scholarship Recommendations", description = "AI Scholarship Recommendations Engine based on eligibility criteria")
public class ScholarshipRecommendationController {

    private final ScholarshipRecommendationService recommendationService;

    @PostMapping("/generate")
    @Operation(summary = "Generate AI scholarship recommendations based on student criteria, assessments, and alignments")
    public ResponseEntity<List<ScholarshipRecommendationResponse>> generateRecommendations(
            @AuthenticationPrincipal User user) {
        log.info("Generating scholarship recommendations for user={}", user.getId());
        return ResponseEntity.ok(recommendationService.generateRecommendations(user.getId()));
    }

    @GetMapping
    @Operation(summary = "Get all recommendations for the authenticated student")
    public ResponseEntity<List<ScholarshipRecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId()));
    }
}
