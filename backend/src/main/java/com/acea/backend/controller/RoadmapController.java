package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.roadmap.*;
import com.acea.backend.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roadmaps")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Roadmaps", description = "AI Personalized Career Roadmap Generator and Progress Tracker")
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/generate")
    @Operation(summary = "Generate a structured AI career learning roadmap consuming previous diagnostic assessment and resource recommendations")
    public ResponseEntity<RoadmapResponse> generateRoadmap(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RoadmapGenerateRequest request) {
        log.info("Requesting roadmap generation for user={}", user.getId());
        return ResponseEntity.ok(roadmapService.generateRoadmap(user.getId(), request));
    }

    @GetMapping
    @Operation(summary = "Get all learning roadmaps for current student")
    public ResponseEntity<List<RoadmapResponse>> getStudentRoadmaps(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roadmapService.getStudentRoadmaps(user.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full structured roadmap profile by ID")
    public ResponseEntity<RoadmapResponse> getRoadmapById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(roadmapService.getRoadmapById(id, user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update roadmap status (e.g., ACTIVE, PAUSED, COMPLETED)")
    public ResponseEntity<RoadmapResponse> updateRoadmap(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(roadmapService.updateRoadmap(id, user.getId(), status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a roadmap")
    public ResponseEntity<Void> deleteRoadmap(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        roadmapService.deleteRoadmap(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks")
    @Operation(summary = "Get tasks list for student filtered by status or due date")
    public ResponseEntity<List<RoadmapTaskResponse>> getStudentTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(roadmapService.getStudentTasks(user.getId(), status, dueDate));
    }

    @PatchMapping("/tasks/{id}")
    @Operation(summary = "Update task completion status (PENDING, IN_PROGRESS, COMPLETED, SKIPPED) or due date")
    public ResponseEntity<RoadmapTaskResponse> updateTaskStatus(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(roadmapService.updateTaskStatus(id, user.getId(), request));
    }

    @GetMapping("/progress")
    @Operation(summary = "Get student overall roadmap progress, completion %, and streak stats")
    public ResponseEntity<RoadmapProgressResponse> getRoadmapProgress(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roadmapService.getRoadmapProgress(user.getId()));
    }

    @PostMapping("/regenerate")
    @Operation(summary = "Dynamically reschedule remaining tasks when student falls behind schedule")
    public ResponseEntity<RoadmapResponse> regenerateRoadmap(
            @AuthenticationPrincipal User user,
            @RequestParam UUID roadmapId) {
        return ResponseEntity.ok(roadmapService.regenerateRoadmap(roadmapId, user.getId()));
    }
}
