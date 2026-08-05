package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.LearningRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearningRecommendationRepository extends JpaRepository<LearningRecommendation, UUID> {

    @Query("SELECT lr FROM LearningRecommendation lr JOIN FETCH lr.resource WHERE lr.student.id = :studentId ORDER BY lr.confidenceScore DESC")
    List<LearningRecommendation> findByStudentIdWithResourceOrderByConfidenceScoreDesc(@Param("studentId") UUID studentId);

    void deleteByStudentId(UUID studentId);
}
