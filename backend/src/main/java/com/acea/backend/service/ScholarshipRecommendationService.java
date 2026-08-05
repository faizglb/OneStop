package com.acea.backend.service;

import com.acea.backend.dto.scholarship.ScholarshipRecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface ScholarshipRecommendationService {

    /** Gathers student data, calls Python FastAPI scholarship eligibility engine, and saves recommendations. */
    List<ScholarshipRecommendationResponse> generateRecommendations(UUID studentId);

    /** Returns saved recommendations. */
    List<ScholarshipRecommendationResponse> getRecommendations(UUID studentId);
}
