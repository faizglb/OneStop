package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.AssessmentResult;
import com.acea.backend.domain.enums.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, UUID> {

    /** Check if an IN_PROGRESS session already exists for student + assessment. */
    Optional<AssessmentResult> findByStudentIdAndAssessmentIdAndStatus(
            UUID studentId, UUID assessmentId, ResultStatus status);

    /** Get all completed results for a student (history). */
    List<AssessmentResult> findByStudentIdAndStatusOrderByCompletedAtDesc(
            UUID studentId, ResultStatus status);

    /** Get all results for a student, ordered by creation date. */
    List<AssessmentResult> findByStudentIdOrderByCreatedAtDesc(UUID studentId);

    /** Fetch result with all eagerly needed relations for the result page. */
    @Query("SELECT ar FROM AssessmentResult ar " +
           "JOIN FETCH ar.assessment a " +
           "JOIN FETCH ar.sectionScores ss " +
           "WHERE ar.id = :id")
    Optional<AssessmentResult> findWithSectionScores(@Param("id") UUID id);

    boolean existsByStudentIdAndAssessmentIdAndStatus(
            UUID studentId, UUID assessmentId, ResultStatus status);
}
