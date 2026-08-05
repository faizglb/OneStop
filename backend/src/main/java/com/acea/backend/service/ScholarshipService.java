package com.acea.backend.service;

import com.acea.backend.dto.scholarship.ScholarshipResponse;
import com.acea.backend.dto.scholarship.ScholarshipApplicationStatusRequest;
import com.acea.backend.dto.scholarship.ScholarshipApplicationHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ScholarshipService {

    /** Returns paginated list of scholarships matching search/filter criteria. */
    Page<ScholarshipResponse> searchScholarships(
            String search,
            String state,
            String type,
            String educationLevel,
            String category,
            BigDecimal minAmount,
            BigDecimal maxIncome,
            UUID studentId,
            Pageable pageable
    );

    /** Returns a single scholarship profile details. */
    ScholarshipResponse getScholarshipById(UUID id, UUID studentId);

    /** Update tracker state (mark as APPLIED, SHORTLISTED, COMPLETED). */
    ScholarshipApplicationHistoryResponse updateApplicationStatus(UUID studentId, ScholarshipApplicationStatusRequest request);

    /** Get tracker updates history. */
    List<ScholarshipApplicationHistoryResponse> getApplicationHistory(UUID studentId);
}
