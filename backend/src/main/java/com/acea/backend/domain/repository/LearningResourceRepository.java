package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.LearningResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResource, UUID> {

    @Query("SELECT r FROM LearningResource r " +
           "WHERE (:search IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.skill) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:platform IS NULL OR LOWER(r.platform) = LOWER(:platform)) " +
           "AND (:difficulty IS NULL OR LOWER(r.difficulty) = LOWER(:difficulty)) " +
           "AND (:category IS NULL OR LOWER(r.category) = LOWER(:category)) " +
           "AND (:isFree IS NULL OR r.isFree = :isFree) " +
           "AND (:certificate = FALSE OR r.certificateAvailable = TRUE)")
    Page<LearningResource> searchResources(
            @Param("search") String search,
            @Param("platform") String platform,
            @Param("difficulty") String difficulty,
            @Param("category") String category,
            @Param("isFree") Boolean isFree,
            @Param("certificate") boolean certificate,
            Pageable pageable
    );
}
