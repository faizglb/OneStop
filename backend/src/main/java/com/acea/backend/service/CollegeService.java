package com.acea.backend.service;

import com.acea.backend.dto.college.CollegeCompareResponse;
import com.acea.backend.dto.college.CollegeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CollegeService {

    /** Returns paginated list of colleges matching search/filter criteria. */
    Page<CollegeResponse> searchColleges(
            String search,
            String state,
            String type,
            BigDecimal maxFees,
            BigDecimal minPlacementPct,
            boolean hostelRequired,
            UUID studentId,
            Pageable pageable
    );

    /** Returns a single college profile details. */
    CollegeResponse getCollegeById(UUID id, UUID studentId);

    /** Collates detailed comparison parameters for up to 3 colleges. */
    CollegeCompareResponse compareColleges(List<UUID> collegeIds);
}
