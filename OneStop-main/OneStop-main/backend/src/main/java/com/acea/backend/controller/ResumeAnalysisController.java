package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.response.ApiResponse;
import com.acea.backend.dto.resume.ResumeAnalysisRequest;
import com.acea.backend.dto.resume.ResumeAnalysisResponse;
import com.acea.backend.dto.resume.ResumeAnalysisSummaryResponse;
import com.acea.backend.service.ResumeAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/resume-analysis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Resume Analysis", description = "AI Resume Analysis and ATS Scoring APIs")
@SecurityRequirement(name = "bearerAuth")
public class ResumeAnalysisController {

    private final ResumeAnalysisService analysisService;

    @PostMapping("/analyze")
    @Operation(summary = "Submit a pre-uploaded resume for AI analysis")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> analyzeResume(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ResumeAnalysisRequest request) {
        log.info("Request to analyze resume '{}' received from user '{}'", request.fileName(), currentUser.getId());
        ResumeAnalysisResponse response = analysisService.analyzeResume(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume analysis completed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get paginated history of past resume analyses")
    public ResponseEntity<ApiResponse<Page<ResumeAnalysisSummaryResponse>>> getAnalysisHistory(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResumeAnalysisSummaryResponse> response = analysisService.getAnalysisHistory(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Analysis history retrieved successfully", response));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get the latest resume analysis result")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> getLatestAnalysis(
            @AuthenticationPrincipal User currentUser) {
        ResumeAnalysisResponse response = analysisService.getLatestAnalysis(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Latest analysis retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed analysis result by ID")
    public ResponseEntity<ApiResponse<ResumeAnalysisResponse>> getAnalysisById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        ResumeAnalysisResponse response = analysisService.getAnalysisById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Analysis details retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume analysis record")
    public ResponseEntity<ApiResponse<Void>> deleteAnalysis(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID id) {
        analysisService.deleteAnalysis(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Resume analysis deleted successfully"));
    }
}
