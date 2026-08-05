package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ScholarshipRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScholarshipRecommendationRepository extends JpaRepository<ScholarshipRecommendation, UUID> {

    @Query("SELECT sr FROM ScholarshipRecommendation sr JOIN FETCH sr.scholarship WHERE sr.student.id = :studentId ORDER BY sr.eligibilityScore DESC")
    List<ScholarshipRecommendation> findByStudentIdWithScholarshipOrderByEligibilityScoreDesc(@Param("studentId") UUID studentId);

    void deleteByStudentId(UUID studentId);
}
