package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ResumeKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResumeKeywordRepository extends JpaRepository<ResumeKeyword, UUID> {

    List<ResumeKeyword> findByAnalysisIdOrderByImportanceAscKeyword(UUID analysisId);

    List<ResumeKeyword> findByAnalysisIdAndFound(UUID analysisId, Boolean found);
}
