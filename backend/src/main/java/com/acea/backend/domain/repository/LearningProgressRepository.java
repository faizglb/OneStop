package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {

    @Query("SELECT lp FROM LearningProgress lp JOIN FETCH lp.resource WHERE lp.student.id = :studentId")
    List<LearningProgress> findByStudentIdWithResource(@Param("studentId") UUID studentId);

    Optional<LearningProgress> findByStudentIdAndResourceId(UUID studentId, UUID resourceId);

    boolean existsByStudentIdAndResourceId(UUID studentId, UUID resourceId);
}
