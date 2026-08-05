package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.CollegeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollegeBookmarkRepository extends JpaRepository<CollegeBookmark, UUID> {

    @Query("SELECT cb FROM CollegeBookmark cb JOIN FETCH cb.college WHERE cb.student.id = :studentId")
    List<CollegeBookmark> findByStudentIdWithCollege(@Param("studentId") UUID studentId);

    Optional<CollegeBookmark> findByStudentIdAndCollegeId(UUID studentId, UUID collegeId);

    boolean existsByStudentIdAndCollegeId(UUID studentId, UUID collegeId);
}
