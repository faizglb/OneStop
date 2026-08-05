package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.Scholarship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, UUID> {

    @Query("SELECT DISTINCT s FROM Scholarship s LEFT JOIN FETCH s.eligibilities WHERE s.id = :id")
    Optional<Scholarship> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT s FROM Scholarship s " +
           "WHERE (:search IS NULL OR LOWER(s.scholarshipName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.provider) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:state IS NULL OR LOWER(s.state) = LOWER(:state)) " +
           "AND (:type IS NULL OR LOWER(s.scholarshipType) = LOWER(:type)) " +
           "AND (:educationLevel IS NULL OR LOWER(s.educationLevel) = LOWER(:educationLevel)) " +
           "AND (:category IS NULL OR LOWER(s.category) = LOWER(:category)) " +
           "AND (:minAmount IS NULL OR s.amount >= :minAmount) " +
           "AND (:maxIncome IS NULL OR s.incomeLimit IS NULL OR s.incomeLimit >= :maxIncome)")
    Page<Scholarship> searchScholarships(
            @Param("search") String search,
            @Param("state") String state,
            @Param("type") String type,
            @Param("educationLevel") String educationLevel,
            @Param("category") String category,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxIncome") BigDecimal maxIncome,
            Pageable pageable
    );
}
