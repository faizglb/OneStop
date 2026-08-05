package com.acea.backend.service;

import com.acea.backend.dto.learning.LearningRecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface LearningRecommendationService {

    /** Gathers student data, calls Python FastAPI resources recommender engine, and saves recommendations. */
    List<LearningRecommendationResponse> generateRecommendations(UUID studentId);

    /** Returns saved recommendations. */
    List<LearningRecommendationResponse> getRecommendations(UUID studentId);
}
