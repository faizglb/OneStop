package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.college.CollegeCompareResponse;
import com.acea.backend.dto.college.CollegeResponse;
import com.acea.backend.service.CollegeBookmarkService;
import com.acea.backend.service.CollegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Colleges", description = "College Search, Filter, Bookmarks, and Comparisons")
public class CollegeController {

    private final CollegeService collegeService;
    private final CollegeBookmarkService bookmarkService;

    // ── Search & Filter ────────────────────────────────────────────────────────

    @GetMapping("/colleges")
    @Operation(summary = "Search and filter colleges with pageable controls")
    public ResponseEntity<Page<CollegeResponse>> searchColleges(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal maxFees,
            @RequestParam(required = false) BigDecimal minPlacementPct,
            @RequestParam(required = false, defaultValue = "false") boolean hostelRequired,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        
        UUID studentId = user != null ? user.getId() : null;
        Page<CollegeResponse> response = collegeService.searchColleges(
                search, state, type, maxFees, minPlacementPct, hostelRequired, studentId, pageable
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/colleges/{id}")
    @Operation(summary = "Get detailed profile of a college by ID")
    public ResponseEntity<CollegeResponse> getCollegeById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        UUID studentId = user != null ? user.getId() : null;
        return ResponseEntity.ok(collegeService.getCollegeById(id, studentId));
    }

    // ── Bookmarks ──────────────────────────────────────────────────────────────

    @PostMapping("/bookmarks")
    @Operation(summary = "Bookmark a college")
    public ResponseEntity<CollegeResponse> bookmarkCollege(
            @AuthenticationPrincipal User user,
            @RequestParam UUID collegeId) {
        log.info("Bookmark request for collegeId={} by user={}", collegeId, user.getId());
        return ResponseEntity.ok(bookmarkService.bookmarkCollege(user.getId(), collegeId));
    }

    @GetMapping("/bookmarks")
    @Operation(summary = "Get all bookmarked colleges")
    public ResponseEntity<List<CollegeResponse>> getBookmarkedColleges(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookmarkService.getBookmarkedColleges(user.getId()));
    }

    @DeleteMapping("/bookmarks/{collegeId}")
    @Operation(summary = "Remove a college bookmark")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable UUID collegeId) {
        log.info("Remove bookmark request for collegeId={} by user={}", collegeId, user.getId());
        bookmarkService.removeBookmark(user.getId(), collegeId);
        return ResponseEntity.noContent().build();
    }

    // ── Comparison ─────────────────────────────────────────────────────────────

    @PostMapping("/compare")
    @Operation(summary = "Compare up to 3 colleges side-by-side")
    public ResponseEntity<CollegeCompareResponse> compareColleges(
            @RequestBody List<UUID> collegeIds) {
        log.info("Comparison requested for collegeIds={}", collegeIds);
        return ResponseEntity.ok(collegeService.compareColleges(collegeIds));
    }
}
