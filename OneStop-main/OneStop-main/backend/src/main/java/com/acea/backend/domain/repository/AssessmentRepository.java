package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.Assessment;
import com.acea.backend.domain.enums.AssessmentStatus;
import com.acea.backend.domain.enums.AssessmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    List<Assessment> findByStatusOrderByAssessmentTypeAsc(AssessmentStatus status);

    Optional<Assessment> findByIdAndStatus(UUID id, AssessmentStatus status);

    boolean existsByAssessmentType(AssessmentType type);
}
