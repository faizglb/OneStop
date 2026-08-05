package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, UUID> {

    List<QuestionCategory> findByAssessmentIdOrderByDisplayOrderAsc(UUID assessmentId);
}
