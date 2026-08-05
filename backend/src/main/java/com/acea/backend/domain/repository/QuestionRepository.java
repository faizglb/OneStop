package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("SELECT q FROM Question q " +
           "JOIN FETCH q.options o " +
           "JOIN FETCH q.category c " +
           "WHERE q.assessment.id = :assessmentId AND q.isActive = true " +
           "ORDER BY q.questionOrder ASC")
    List<Question> findActiveQuestionsWithOptions(@Param("assessmentId") UUID assessmentId);

    List<Question> findByAssessmentIdAndIsActiveTrueOrderByQuestionOrderAsc(UUID assessmentId);

    boolean existsByIdAndAssessmentId(UUID questionId, UUID assessmentId);
}
