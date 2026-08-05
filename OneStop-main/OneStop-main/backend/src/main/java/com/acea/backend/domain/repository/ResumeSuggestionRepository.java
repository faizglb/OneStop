package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ResumeSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResumeSuggestionRepository extends JpaRepository<ResumeSuggestion, UUID> {

    List<ResumeSuggestion> findByAnalysisIdOrderByPriorityAscCategory(UUID analysisId);
}
