package com.acea.backend.service;

import com.acea.backend.domain.entity.AssessmentResult;
import com.acea.backend.domain.entity.ResultAnswer;
import com.acea.backend.dto.assessment.SectionScoreResponse;
import com.acea.backend.dto.assessment.TraitScoreResponse;

import java.util.List;

/**
 * Pure scoring logic contract.
 * Stateless — receives data, returns computed scores.
 * Implementations MUST NOT perform recommendation or career matching.
 */
public interface ScoringEngine {

    /**
     * Calculates section scores for an aptitude result and persists them.
     * @param result the result entity (answers already saved)
     * @param answers all saved answers for this result
     * @return persisted section score summaries
     */
    List<SectionScoreResponse> scoreAptitude(AssessmentResult result, List<ResultAnswer> answers);

    /**
     * Calculates Big Five trait scores and persists them.
     * @param result the result entity
     * @param answers all saved answers
     * @return list of trait score summaries
     */
    List<TraitScoreResponse> scorePersonality(AssessmentResult result, List<ResultAnswer> answers);

    /**
     * Calculates RIASEC interest scores and persists them.
     * @param result the result entity
     * @param answers all saved answers
     * @return list of trait score summaries ordered by score descending
     */
    List<TraitScoreResponse> scoreInterest(AssessmentResult result, List<ResultAnswer> answers);
}
