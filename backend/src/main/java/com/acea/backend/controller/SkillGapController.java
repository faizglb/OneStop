package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.skillgap.*;
import com.acea.backend.security.UserPrincipal;
import com.acea.backend.service.SkillGapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skill-gap")
@RequiredArgsConstructor
@Tag(name = "Skill Gap Analysis", description = "AI Skill Gap Engine endpoints for comparing student abilities with career requirements")
@SecurityRequirement(name = "bearerAuth")
public class SkillGapController {

    private final SkillGapService skillGapService;

    @PostMapping("/analyze")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Analyze Skill Gap", description = "Run AI Skill Gap analysis comparing current profile, assessments, and resume against target career requirements.")
    public ResponseEntity<SkillGapReportResponse> analyzeSkillGap(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SkillGapAnalyzeRequest request
    ) {
        User currentUser = principal.getUser();
        SkillGapReportResponse response = skillGapService.analyzeSkillGap(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Latest Skill Gap Report", description = "Retrieve the latest AI Skill Gap Analysis Report for the authenticated student.")
    public ResponseEntity<SkillGapReportResponse> getLatestReport(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.getUser();
        SkillGapReportResponse response = skillGapService.getLatestReport(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Skill Gap History", description = "Retrieve historical skill gap reports for the authenticated student.")
    public ResponseEntity<List<SkillGapReportResponse>> getReportHistory(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.getUser();
        List<SkillGapReportResponse> history = skillGapService.getReportHistory(currentUser);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/missing-skills")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Missing Skills", description = "Get list of missing or weak skills for the target career.")
    public ResponseEntity<List<SkillGapDto>> getMissingSkills(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.getUser();
        List<SkillGapDto> missingSkills = skillGapService.getMissingSkills(currentUser);
        return ResponseEntity.ok(missingSkills);
    }

    @GetMapping("/readiness")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Readiness Breakdown", description = "Get multi-category job readiness scores out of 100.")
    public ResponseEntity<ReadinessBreakdownDto> getReadinessBreakdown(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.getUser();
        ReadinessBreakdownDto readiness = skillGapService.getReadinessBreakdown(currentUser);
        return ResponseEntity.ok(readiness);
    }

    @PostMapping("/regenerate")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Regenerate Skill Gap Report", description = "Force re-analysis of skill gaps with latest student data.")
    public ResponseEntity<SkillGapReportResponse> regenerateReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SkillGapAnalyzeRequest request
    ) {
        User currentUser = principal.getUser();
        SkillGapReportResponse response = skillGapService.regenerateReport(currentUser, request);
        return ResponseEntity.ok(response);
    }
}
