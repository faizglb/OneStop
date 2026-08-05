package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.assessment.*;
import com.acea.backend.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API for the AI Assessment Module.
 * All endpoints require JWT authentication.
 *
 * Base path: /assessments  (context-path /api/v1 is applied globally)
 */
@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Assessments", description = "Aptitude, Personality, and Interest assessment lifecycle")
public class AssessmentController {

    private final AssessmentService assessmentService;

    // ── Discovery ──────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all available assessments")
    public ResponseEntity<List<AssessmentSummaryResponse>> getAvailableAssessments() {
        return ResponseEntity.ok(assessmentService.getAvailableAssessments());
    }

    @GetMapping("/{assessmentId}")
    @Operation(summary = "Get assessment info (for info page before starting)")
    public ResponseEntity<AssessmentSummaryResponse> getAssessmentById(
            @PathVariable UUID assessmentId) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(assessmentId));
    }

    // ── Session ────────────────────────────────────────────────────────────────

    @PostMapping("/start")
    @Operation(summary = "Start or resume an assessment session")
    public ResponseEntity<StartSessionResponse> startAssessment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StartAssessmentRequest request) {
        log.info("Start assessment request: assessmentId={} userId={}", request.assessmentId(), user.getId());
        StartSessionResponse response = assessmentService.startAssessment(user.getId(), request);
        HttpStatus status = response.resumed() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/save-answer")
    @Operation(summary = "Autosave a single answer (idempotent)")
    public ResponseEntity<Void> saveAnswer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SaveAnswerRequest request) {
        assessmentService.saveAnswer(user.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit assessment for scoring")
    public ResponseEntity<AssessmentResultResponse> submitAssessment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmitAssessmentRequest request) {
        log.info("Submit assessment: resultId={} userId={}", request.resultId(), user.getId());
        return ResponseEntity.ok(assessmentService.submitAssessment(user.getId(), request));
    }

    // ── Results ────────────────────────────────────────────────────────────────

    @GetMapping("/result/{resultId}")
    @Operation(summary = "Get a specific scored result")
    public ResponseEntity<AssessmentResultResponse> getResult(
            @AuthenticationPrincipal User user,
            @PathVariable UUID resultId) {
        return ResponseEntity.ok(assessmentService.getResult(user.getId(), resultId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get all assessment attempts for the current user")
    public ResponseEntity<List<AssessmentHistoryResponse>> getHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(assessmentService.getHistory(user.getId()));
    }
}
