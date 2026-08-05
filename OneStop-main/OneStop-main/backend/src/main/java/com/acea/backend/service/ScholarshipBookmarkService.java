package com.acea.backend.service;

import com.acea.backend.dto.scholarship.ScholarshipResponse;

import java.util.List;
import java.util.UUID;

public interface ScholarshipBookmarkService {

    /** Bookmark a scholarship. Throws validation exception if already bookmarked. */
    ScholarshipResponse bookmarkScholarship(UUID studentId, UUID scholarshipId);

    /** Get all bookmarked scholarships. */
    List<ScholarshipResponse> getBookmarkedScholarships(UUID studentId);

    /** Remove a bookmark. */
    void removeBookmark(UUID studentId, UUID scholarshipId);
}
