package com.acea.backend.service;

import com.acea.backend.dto.recommendation.RecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {

    /**
     * Gathers all profile and assessment data for the student, communicates
     * with the FastAPI AI Service, saves recommendations in database, and returns them.
     */
    List<RecommendationResponse> generateRecommendations(UUID studentId);

    /** Returns all generated recommendations for the student. */
    List<RecommendationResponse> getRecommendations(UUID studentId);

    /** Returns career recommendations list. */
    List<RecommendationResponse> getCareers(UUID studentId);

    /** Returns list of recommended skills. */
    List<RecommendationResponse.RecommendedSkillResponse> getSkills(UUID studentId);

    /** Returns list of recommended certifications. */
    List<RecommendationResponse.RecommendedCertificationResponse> getCertifications(UUID studentId);

    /** Returns list of recommended projects. */
    List<RecommendationResponse.RecommendedProjectResponse> getProjects(UUID studentId);

    /** Toggles the bookmark status of a recommendation. */
    RecommendationResponse toggleBookmark(UUID studentId, UUID recommendationId);

    /** Deletes all recommendations for the student (for regeneration). */
    void deleteRecommendations(UUID studentId);
}
