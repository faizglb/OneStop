package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.recommendation.RecommendationResponse;
import com.acea.backend.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recommendations", description = "AI Recommendations for Careers, Skills, Certifications, and Projects")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/generate")
    @Operation(summary = "Generate fresh career recommendations using student's profile & assessment scores")
    public ResponseEntity<List<RecommendationResponse>> generateRecommendations(
            @AuthenticationPrincipal User user) {
        log.info("Generate recommendations requested by studentId={}", user.getId());
        List<RecommendationResponse> response = recommendationService.generateRecommendations(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all recommendations for the authenticated student")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId()));
    }

    @GetMapping("/careers")
    @Operation(summary = "Get career recommendations")
    public ResponseEntity<List<RecommendationResponse>> getCareers(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getCareers(user.getId()));
    }

    @GetMapping("/skills")
    @Operation(summary = "Get list of recommended skills")
    public ResponseEntity<List<RecommendationResponse.RecommendedSkillResponse>> getSkills(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getSkills(user.getId()));
    }

    @GetMapping("/certifications")
    @Operation(summary = "Get list of recommended certifications")
    public ResponseEntity<List<RecommendationResponse.RecommendedCertificationResponse>> getCertifications(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getCertifications(user.getId()));
    }

    @GetMapping("/projects")
    @Operation(summary = "Get list of recommended projects")
    public ResponseEntity<List<RecommendationResponse.RecommendedProjectResponse>> getProjects(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getProjects(user.getId()));
    }

    @PatchMapping("/bookmark/{id}")
    @Operation(summary = "Toggle recommendation bookmark status")
    public ResponseEntity<RecommendationResponse> toggleBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        log.info("Toggle bookmark requested for recommendationId={} by studentId={}", id, user.getId());
        RecommendationResponse response = recommendationService.toggleBookmark(user.getId(), id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/regenerate")
    @Operation(summary = "Clear recommendations history to allow regeneration")
    public ResponseEntity<Void> deleteRecommendations(
            @AuthenticationPrincipal User user) {
        log.info("Clear recommendations requested by studentId={}", user.getId());
        recommendationService.deleteRecommendations(user.getId());
        return ResponseEntity.noContent().build();
    }
}
