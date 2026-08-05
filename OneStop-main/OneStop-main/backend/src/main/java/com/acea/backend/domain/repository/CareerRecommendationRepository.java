package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.CareerRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CareerRecommendationRepository extends JpaRepository<CareerRecommendation, UUID> {

    List<CareerRecommendation> findByStudentIdOrderByConfidenceScoreDesc(UUID studentId);

    List<CareerRecommendation> findByStudentIdAndIsBookmarkedTrue(UUID studentId);

    void deleteByStudentId(UUID studentId);

    @Query("SELECT cr FROM CareerRecommendation cr " +
           "LEFT JOIN FETCH cr.skills s " +
           "LEFT JOIN FETCH cr.certifications c " +
           "LEFT JOIN FETCH cr.projects p " +
           "WHERE cr.id = :id")
    Optional<CareerRecommendation> findByIdWithDetails(@Param("id") UUID id);
}
