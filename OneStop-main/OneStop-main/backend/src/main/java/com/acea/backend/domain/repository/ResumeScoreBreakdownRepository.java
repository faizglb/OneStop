package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ResumeScoreBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeScoreBreakdownRepository extends JpaRepository<ResumeScoreBreakdown, UUID> {

    Optional<ResumeScoreBreakdown> findByAnalysisId(UUID analysisId);
}
