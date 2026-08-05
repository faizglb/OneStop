package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.learning.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.LearningResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningResourceServiceImpl implements LearningResourceService {

    private final LearningResourceRepository resourceRepository;
    private final LearningBookmarkRepository bookmarkRepository;
    private final LearningProgressRepository progressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LearningResourceResponse> searchResources(
            String search,
            String platform,
            String difficulty,
            String category,
            Boolean isFree,
            boolean certificateRequired,
            UUID studentId,
            Pageable pageable) {

        log.debug("Searching learning resources with search={}, platform={}", search, platform);
        Page<LearningResource> page = resourceRepository.searchResources(
                search, platform, difficulty, category, isFree, certificateRequired, pageable
        );

        return page.map(r -> toResponse(r, studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public LearningResourceResponse getResourceById(UUID id, UUID studentId) {
        LearningResource r = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning resource not found: " + id));

        return toResponse(r, studentId);
    }

    @Override
    @Transactional
    public LearningResourceResponse bookmarkResource(UUID studentId, UUID resourceId) {
        log.info("Bookmarking resourceId={} for studentId={}", resourceId, studentId);

        // Check duplicate
        if (bookmarkRepository.existsByStudentIdAndResourceId(studentId, resourceId)) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "This learning resource is already bookmarked.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found."));

        LearningResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning resource not found."));

        LearningBookmark bookmark = LearningBookmark.builder()
                .student(student)
                .resource(resource)
                .build();

        bookmarkRepository.save(bookmark);
        return getResourceById(resourceId, studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningResourceResponse> getBookmarkedResources(UUID studentId) {
        return bookmarkRepository.findByStudentIdWithResource(studentId)
                .stream()
                .map(lb -> getResourceById(lb.getResource().getId(), studentId))
                .toList();
    }

    @Override
    @Transactional
    public void removeBookmark(UUID studentId, UUID resourceId) {
        log.info("Removing bookmark for resourceId={} and studentId={}", resourceId, studentId);
        LearningBookmark bookmark = bookmarkRepository.findByStudentIdAndResourceId(studentId, resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found."));

        bookmarkRepository.delete(bookmark);
    }

    @Override
    @Transactional
    public LearningProgressResponse updateProgress(UUID studentId, LearningProgressRequest request) {
        log.info("Updating progress for studentId={}, resourceId={}, status={}, percentage={}",
                studentId, request.resourceId(), request.status(), request.progressPercentage());

        // Validate percentage bounds
        if (request.progressPercentage() < 0 || request.progressPercentage() > 100) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "Progress percentage must be between 0 and 100.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found."));

        LearningResource resource = resourceRepository.findById(request.resourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Learning resource not found."));

        LearningProgress progress = progressRepository
                .findByStudentIdAndResourceId(studentId, request.resourceId())
                .orElseGet(() -> LearningProgress.builder().student(student).resource(resource).startedAt(Instant.now()).build());

        progress.setProgressPercentage(request.progressPercentage());
        progress.setStatus(request.status());
        progress.setLastAccessed(Instant.now());

        if (request.progressPercentage() == 100 || "COMPLETED".equalsIgnoreCase(request.status())) {
            progress.setProgressPercentage(100);
            progress.setStatus("COMPLETED");
            progress.setCompletedAt(Instant.now());
        }

        LearningProgress saved = progressRepository.save(progress);
        return toProgressResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getProgressHistory(UUID studentId) {
        return progressRepository.findByStudentIdWithResource(studentId)
                .stream()
                .map(this::toProgressResponse)
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private LearningResourceResponse toResponse(LearningResource r, UUID studentId) {
        boolean isBookmarked = false;
        String status = null;
        Integer progress = null;
        if (studentId != null) {
            isBookmarked = bookmarkRepository.existsByStudentIdAndResourceId(studentId, r.getId());
            Optional<LearningProgress> lp = progressRepository.findByStudentIdAndResourceId(studentId, r.getId());
            if (lp.isPresent()) {
                status = lp.get().getStatus();
                progress = lp.get().getProgressPercentage();
            }
        }

        return new LearningResourceResponse(
                r.getId(), r.getTitle(), r.getDescription(), r.getPlatform(), r.getResourceType(),
                r.getCategory(), r.getSkill(), r.getDifficulty(), r.getDuration(), r.getLanguage(),
                r.getIsFree(), r.getPrice(), r.getRating(), r.getThumbnail(), r.getResourceUrl(),
                r.getAuthor(), r.getProvider(), r.getCertificateAvailable(), isBookmarked, status, progress
        );
    }

    private LearningProgressResponse toProgressResponse(LearningProgress lp) {
        return new LearningProgressResponse(
                lp.getId(), lp.getResource().getId(), lp.getResource().getTitle(), lp.getResource().getPlatform(),
                lp.getResource().getCategory(), lp.getStatus(), lp.getProgressPercentage(),
                lp.getStartedAt(), lp.getCompletedAt(), lp.getLastAccessed()
        );
    }
}
