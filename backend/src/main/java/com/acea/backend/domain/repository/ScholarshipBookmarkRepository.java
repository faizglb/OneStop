package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ScholarshipBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScholarshipBookmarkRepository extends JpaRepository<ScholarshipBookmark, UUID> {

    @Query("SELECT sb FROM ScholarshipBookmark sb JOIN FETCH sb.scholarship WHERE sb.student.id = :studentId")
    List<ScholarshipBookmark> findByStudentIdWithScholarship(@Param("studentId") UUID studentId);

    Optional<ScholarshipBookmark> findByStudentIdAndScholarshipId(UUID studentId, UUID scholarshipId);

    boolean existsByStudentIdAndScholarshipId(UUID studentId, UUID scholarshipId);
}
