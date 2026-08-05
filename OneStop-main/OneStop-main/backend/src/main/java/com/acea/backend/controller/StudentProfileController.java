package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;
import com.acea.backend.service.StudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Tag(name = "Student Profile", description = "Student Profile Management System APIs")
@SecurityRequirement(name = "bearerAuth")
public class StudentProfileController {

    private final StudentProfileService profileService;

    // ── Profile CRUD ──────────────────────────────────────────────────────────

    @GetMapping("/profile")
    @Operation(summary = "Get current user's profile details")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(
            @AuthenticationPrincipal User currentUser) {
        StudentProfileResponse response = profileService.getProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    @PutMapping("/profile")
    @Operation(summary = "Create or fully update student profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody StudentProfileRequest request) {
        StudentProfileResponse response = profileService.createOrUpdateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile saved successfully", response));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "Delete student profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @AuthenticationPrincipal User currentUser) {
        profileService.deleteProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully"));
    }

    // ── Skills CRUD ───────────────────────────────────────────────────────────

    @PostMapping("/skills")
    @Operation(summary = "Add a new skill to student profile")
    public ResponseEntity<ApiResponse<SkillResponse>> addSkill(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SkillRequest request) {
        SkillResponse response = profileService.addSkill(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Skill added successfully", response));
    }

    @GetMapping("/skills")
    @Operation(summary = "Get list of student's skills")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getSkills(
            @AuthenticationPrincipal User currentUser) {
        List<SkillResponse> response = profileService.getSkills(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved successfully", response));
    }

    @PutMapping("/skills/{id}")
    @Operation(summary = "Update skill details")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID skillId,
            @Valid @RequestBody SkillRequest request) {
        SkillResponse response = profileService.updateSkill(currentUser.getId(), skillId, request);
        return ResponseEntity.ok(ApiResponse.success("Skill updated successfully", response));
    }

    @DeleteMapping("/skills/{id}")
    @Operation(summary = "Delete a skill")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID skillId) {
        profileService.deleteSkill(currentUser.getId(), skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted successfully"));
    }

    // ── Interests CRUD ────────────────────────────────────────────────────────

    @PostMapping("/interests")
    @Operation(summary = "Add an interest")
    public ResponseEntity<ApiResponse<InterestResponse>> addInterest(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody InterestRequest request) {
        InterestResponse response = profileService.addInterest(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Interest added successfully", response));
    }

    @GetMapping("/interests")
    @Operation(summary = "Get student's interests")
    public ResponseEntity<ApiResponse<List<InterestResponse>>> getInterests(
            @AuthenticationPrincipal User currentUser) {
        List<InterestResponse> response = profileService.getInterests(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Interests retrieved successfully", response));
    }

    @DeleteMapping("/interests/{id}")
    @Operation(summary = "Delete an interest")
    public ResponseEntity<ApiResponse<Void>> deleteInterest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID interestId) {
        profileService.deleteInterest(currentUser.getId(), interestId);
        return ResponseEntity.ok(ApiResponse.success("Interest deleted successfully"));
    }

    // ── Goals CRUD ────────────────────────────────────────────────────────────

    @PostMapping("/goals")
    @Operation(summary = "Add a career goal")
    public ResponseEntity<ApiResponse<GoalResponse>> addGoal(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = profileService.addGoal(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Goal added successfully", response));
    }

    @GetMapping("/goals")
    @Operation(summary = "Get list of career goals")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getGoals(
            @AuthenticationPrincipal User currentUser) {
        List<GoalResponse> response = profileService.getGoals(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", response));
    }

    @PutMapping("/goals/{id}")
    @Operation(summary = "Update career goal")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID goalId,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = profileService.updateGoal(currentUser.getId(), goalId, request);
        return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", response));
    }

    @DeleteMapping("/goals/{id}")
    @Operation(summary = "Delete a career goal")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID goalId) {
        profileService.deleteGoal(currentUser.getId(), goalId);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully"));
    }

    // ── Projects CRUD ─────────────────────────────────────────────────────────

    @PostMapping("/projects")
    @Operation(summary = "Add a project")
    public ResponseEntity<ApiResponse<ProjectResponse>> addProject(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = profileService.addProject(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Project added successfully", response));
    }

    @GetMapping("/projects")
    @Operation(summary = "Get list of projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(
            @AuthenticationPrincipal User currentUser) {
        List<ProjectResponse> response = profileService.getProjects(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Projects retrieved successfully", response));
    }

    @PutMapping("/projects/{id}")
    @Operation(summary = "Update project details")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID projectId,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = profileService.updateProject(currentUser.getId(), projectId, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    @DeleteMapping("/projects/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID projectId) {
        profileService.deleteProject(currentUser.getId(), projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }

    // ── Certifications CRUD ───────────────────────────────────────────────────

    @PostMapping("/certificates")
    @Operation(summary = "Add a certificate")
    public ResponseEntity<ApiResponse<CertificationResponse>> addCertification(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CertificationRequest request) {
        CertificationResponse response = profileService.addCertification(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Certification added successfully", response));
    }

    @GetMapping("/certificates")
    @Operation(summary = "Get list of certificates")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> getCertifications(
            @AuthenticationPrincipal User currentUser) {
        List<CertificationResponse> response = profileService.getCertifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Certifications retrieved successfully", response));
    }

    @DeleteMapping("/certificates/{id}")
    @Operation(summary = "Delete a certificate")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID certificationId) {
        profileService.deleteCertification(currentUser.getId(), certificationId);
        return ResponseEntity.ok(ApiResponse.success("Certification deleted successfully"));
    }
}
