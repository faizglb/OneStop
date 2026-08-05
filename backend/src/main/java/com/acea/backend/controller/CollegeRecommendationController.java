package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.college.CollegeRecommendationRequest;
import com.acea.backend.dto.college.CollegeRecommendationResponse;
import com.acea.backend.service.CollegeRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college-recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "College Recommendations", description = "AI College Recommendations Engine based on entrance ranks and categories")
public class CollegeRecommendationController {

    private final CollegeRecommendationService recommendationService;

    @PostMapping("/generate")
    @Operation(summary = "Generate AI College recommendations matching budget, ranks, state preferences, and career roadmap alignments")
    public ResponseEntity<List<CollegeRecommendationResponse>> generateRecommendations(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CollegeRecommendationRequest request) {
        log.info("Generate college recommendations requested by user={}", user.getId());
        List<CollegeRecommendationResponse> response = recommendationService.generateRecommendations(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all recommendations for the authenticated student")
    public ResponseEntity<List<CollegeRecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId()));
    }
}
