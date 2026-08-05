package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.scholarship.ScholarshipResponse;
import com.acea.backend.dto.scholarship.ScholarshipApplicationStatusRequest;
import com.acea.backend.dto.scholarship.ScholarshipApplicationHistoryResponse;
import com.acea.backend.service.ScholarshipBookmarkService;
import com.acea.backend.service.ScholarshipService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/scholarships")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Scholarships", description = "Scholarships Search, Shortlists, Bookmarks, and Applications Tracker")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;
    private final ScholarshipBookmarkService bookmarkService;

    // ── Search & Filter ────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Search and filter active scholarships database")
    public ResponseEntity<Page<ScholarshipResponse>> searchScholarships(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxIncome,
            @AuthenticationPrincipal User user,
            Pageable pageable) {

        UUID studentId = user != null ? user.getId() : null;
        Page<ScholarshipResponse> page = scholarshipService.searchScholarships(
                search, state, type, educationLevel, category, minAmount, maxIncome, studentId, pageable
        );
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single scholarship detailed profile by ID")
    public ResponseEntity<ScholarshipResponse> getScholarshipById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        UUID studentId = user != null ? user.getId() : null;
        return ResponseEntity.ok(scholarshipService.getScholarshipById(id, studentId));
    }

    // ── Bookmarks ──────────────────────────────────────────────────────────────

    @PostMapping("/bookmark")
    @Operation(summary = "Bookmark a scholarship")
    public ResponseEntity<ScholarshipResponse> bookmarkScholarship(
            @AuthenticationPrincipal User user,
            @RequestParam UUID scholarshipId) {
        return ResponseEntity.ok(bookmarkService.bookmarkScholarship(user.getId(), scholarshipId));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "Get all student bookmarked scholarships")
    public ResponseEntity<List<ScholarshipResponse>> getBookmarkedScholarships(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookmarkService.getBookmarkedScholarships(user.getId()));
    }

    @DeleteMapping("/bookmarks/{id}")
    @Operation(summary = "Remove a scholarship bookmark")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        bookmarkService.removeBookmark(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    // ── Applications Tracker ───────────────────────────────────────────────────

    @PostMapping("/application-status")
    @Operation(summary = "Mark application tracking state (Applied, Shortlisted, Completed)")
    public ResponseEntity<ScholarshipApplicationHistoryResponse> updateApplicationStatus(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ScholarshipApplicationStatusRequest request) {
        return ResponseEntity.ok(scholarshipService.updateApplicationStatus(user.getId(), request));
    }

    @GetMapping("/application-history")
    @Operation(summary = "Get application tracking boards status log history")
    public ResponseEntity<List<ScholarshipApplicationHistoryResponse>> getApplicationHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(scholarshipService.getApplicationHistory(user.getId()));
    }
}
