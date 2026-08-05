package com.acea.backend.service;

import com.acea.backend.dto.learning.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LearningResourceService {

    /** Returns paginated list of resources matching search/filter criteria. */
    Page<LearningResourceResponse> searchResources(
            String search,
            String platform,
            String difficulty,
            String category,
            Boolean isFree,
            boolean certificateRequired,
            UUID studentId,
            Pageable pageable
    );

    /** Returns a single learning resource profile details. */
    LearningResourceResponse getResourceById(UUID id, UUID studentId);

    /** Bookmark a learning resource. */
    LearningResourceResponse bookmarkResource(UUID studentId, UUID resourceId);

    /** Get all bookmarked learning resources. */
    List<LearningResourceResponse> getBookmarkedResources(UUID studentId);

    /** Remove bookmark. */
    void removeBookmark(UUID studentId, UUID resourceId);

    /** Update tracker progress (percentage, started/completed flags). */
    LearningProgressResponse updateProgress(UUID studentId, LearningProgressRequest request);

    /** Get student learning progress list. */
    List<LearningProgressResponse> getProgressHistory(UUID studentId);
}
