package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.learning.*;
import com.acea.backend.service.LearningResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Resources", description = "Learning Resources Search, Bookmarks, and Progress Tracking")
public class LearningResourceController {

    private final LearningResourceService resourceService;

    // ── Search & Filter ────────────────────────────────────────────────────────

    @GetMapping("/resources")
    @Operation(summary = "Search and filter educational resources database")
    public ResponseEntity<Page<LearningResourceResponse>> searchResources(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false, defaultValue = "false") boolean certificateRequired,
            @AuthenticationPrincipal User user,
            Pageable pageable) {

        UUID studentId = user != null ? user.getId() : null;
        Page<LearningResourceResponse> page = resourceService.searchResources(
                search, platform, difficulty, category, isFree, certificateRequired, studentId, pageable
        );
        return ResponseEntity.ok(page);
    }

    @GetMapping("/resources/{id}")
    @Operation(summary = "Get detailed course metrics by resource ID")
    public ResponseEntity<LearningResourceResponse> getResourceById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        UUID studentId = user != null ? user.getId() : null;
        return ResponseEntity.ok(resourceService.getResourceById(id, studentId));
    }

    // ── Bookmarks ──────────────────────────────────────────────────────────────

    @PostMapping("/resources/bookmark")
    @Operation(summary = "Bookmark a learning resource")
    public ResponseEntity<LearningResourceResponse> bookmarkResource(
            @AuthenticationPrincipal User user,
            @RequestParam UUID resourceId) {
        return ResponseEntity.ok(resourceService.bookmarkResource(user.getId(), resourceId));
    }

    @GetMapping("/resources/bookmarks")
    @Operation(summary = "Get all bookmarked learning courses")
    public ResponseEntity<List<LearningResourceResponse>> getBookmarkedResources(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(resourceService.getBookmarkedResources(user.getId()));
    }

    @DeleteMapping("/resources/bookmarks/{resourceId}")
    @Operation(summary = "Remove a learning resource bookmark")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable UUID resourceId) {
        resourceService.removeBookmark(user.getId(), resourceId);
        return ResponseEntity.noContent().build();
    }

    // ── Progress Tracking ──────────────────────────────────────────────────────

    @PostMapping("/learning-progress")
    @Operation(summary = "Log course completion percentage status")
    public ResponseEntity<LearningProgressResponse> updateProgress(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody LearningProgressRequest request) {
        return ResponseEntity.ok(resourceService.updateProgress(user.getId(), request));
    }

    @GetMapping("/learning-progress")
    @Operation(summary = "Get student currently continuing courses progress log")
    public ResponseEntity<List<LearningProgressResponse>> getProgressHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(resourceService.getProgressHistory(user.getId()));
    }
}
