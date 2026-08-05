package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ResumeAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, UUID> {

    Page<ResumeAnalysis> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<ResumeAnalysis> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT r FROM ResumeAnalysis r WHERE r.userId = :userId ORDER BY r.createdAt DESC LIMIT 1")
    Optional<ResumeAnalysis> findLatestByUserId(UUID userId);

    long countByUserId(UUID userId);
}
