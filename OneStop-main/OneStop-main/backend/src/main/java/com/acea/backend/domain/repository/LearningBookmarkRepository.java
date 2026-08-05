package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.LearningBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningBookmarkRepository extends JpaRepository<LearningBookmark, UUID> {

    @Query("SELECT lb FROM LearningBookmark lb JOIN FETCH lb.resource WHERE lb.student.id = :studentId")
    List<LearningBookmark> findByStudentIdWithResource(@Param("studentId") UUID studentId);

    Optional<LearningBookmark> findByStudentIdAndResourceId(UUID studentId, UUID resourceId);

    boolean existsByStudentIdAndResourceId(UUID studentId, UUID resourceId);
}
