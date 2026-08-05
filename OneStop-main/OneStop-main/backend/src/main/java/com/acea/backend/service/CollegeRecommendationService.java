package com.acea.backend.service;

import com.acea.backend.dto.college.CollegeRecommendationRequest;
import com.acea.backend.dto.college.CollegeRecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface CollegeRecommendationService {

    /** Gathers student data, calls Python FastAPI matching service, and saves top college recommendations. */
    List<CollegeRecommendationResponse> generateRecommendations(UUID studentId, CollegeRecommendationRequest request);

    /** Returns all generated college recommendations for the student. */
    List<CollegeRecommendationResponse> getRecommendations(UUID studentId);
}
