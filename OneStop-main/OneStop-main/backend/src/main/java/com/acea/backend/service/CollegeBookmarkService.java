package com.acea.backend.service;

import com.acea.backend.dto.college.CollegeResponse;

import java.util.List;
import java.util.UUID;

public interface CollegeBookmarkService {

    /** Bookmark a college. Throws duplicate exception if already bookmarked. */
    CollegeResponse bookmarkCollege(UUID studentId, UUID collegeId);

    /** Get all bookmarked colleges for the student. */
    List<CollegeResponse> getBookmarkedColleges(UUID studentId);

    /** Remove a bookmark by college ID. */
    void removeBookmark(UUID studentId, UUID collegeId);
}
