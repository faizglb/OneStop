package com.acea.backend.service;

import com.acea.backend.dto.resume.ResumeAnalysisRequest;
import com.acea.backend.dto.resume.ResumeAnalysisResponse;
import com.acea.backend.dto.resume.ResumeAnalysisSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Core service for triggering AI resume analysis, persisting results,
 * and retrieving analysis history for a student.
 */
public interface ResumeAnalysisService {

    /**
     * Uploads the resume URL to the AI service, persists all analysis results
     * (score breakdown, sections, keywords, suggestions) and returns the full response.
     */
    ResumeAnalysisResponse analyzeResume(UUID userId, ResumeAnalysisRequest request);

    /**
     * Returns paginated summary of all past analyses for this student, newest first.
     */
    Page<ResumeAnalysisSummaryResponse> getAnalysisHistory(UUID userId, Pageable pageable);

    /**
     * Returns the most recent completed analysis for this student.
     */
    ResumeAnalysisResponse getLatestAnalysis(UUID userId);

    /**
     * Returns a specific analysis by ID — validates ownership.
     */
    ResumeAnalysisResponse getAnalysisById(UUID userId, UUID analysisId);

    /**
     * Permanently deletes an analysis record and all its sub-records.
     */
    void deleteAnalysis(UUID userId, UUID analysisId);
}
