package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ResumeSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResumeSectionRepository extends JpaRepository<ResumeSection, UUID> {

    List<ResumeSection> findByAnalysisIdOrderBySectionName(UUID analysisId);
}
