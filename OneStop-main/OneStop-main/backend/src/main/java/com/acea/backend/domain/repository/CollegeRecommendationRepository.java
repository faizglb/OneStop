package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.CollegeRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CollegeRecommendationRepository extends JpaRepository<CollegeRecommendation, UUID> {

    @Query("SELECT cr FROM CollegeRecommendation cr JOIN FETCH cr.college WHERE cr.student.id = :studentId ORDER BY cr.confidenceScore DESC")
    List<CollegeRecommendation> findByStudentIdWithCollegeOrderByConfidenceDesc(@Param("studentId") UUID studentId);

    void deleteByStudentId(UUID studentId);
}
