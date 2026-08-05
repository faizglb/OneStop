package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.College;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollegeRepository extends JpaRepository<College, UUID> {

    @Query("SELECT DISTINCT c FROM College c " +
           "LEFT JOIN FETCH c.courses co " +
           "LEFT JOIN FETCH c.facilities f " +
           "WHERE c.id = :id")
    Optional<College> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT c FROM College c " +
           "WHERE (:search IS NULL OR LOWER(c.collegeName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.city) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:state IS NULL OR LOWER(c.state) = LOWER(:state)) " +
           "AND (:type IS NULL OR LOWER(c.collegeType) = LOWER(:type)) " +
           "AND (:maxFees IS NULL OR c.annualFees <= :maxFees) " +
           "AND (:minPlacementPct IS NULL OR c.placementPercentage >= :minPlacementPct) " +
           "AND (:hostelRequired = FALSE OR c.hostelAvailable = TRUE)")
    Page<College> searchColleges(
            @Param("search") String search,
            @Param("state") String state,
            @Param("type") String type,
            @Param("maxFees") BigDecimal maxFees,
            @Param("minPlacementPct") BigDecimal minPlacementPct,
            @Param("hostelRequired") boolean hostelRequired,
            Pageable pageable
    );
}
