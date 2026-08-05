package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ScholarshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScholarshipApplicationRepository extends JpaRepository<ScholarshipApplication, UUID> {

    @Query("SELECT sa FROM ScholarshipApplication sa JOIN FETCH sa.scholarship WHERE sa.student.id = :studentId")
    List<ScholarshipApplication> findByStudentIdWithScholarship(@Param("studentId") UUID studentId);

    Optional<ScholarshipApplication> findByStudentIdAndScholarshipId(UUID studentId, UUID scholarshipId);

    boolean existsByStudentIdAndScholarshipId(UUID studentId, UUID scholarshipId);
}
