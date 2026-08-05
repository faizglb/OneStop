package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RecommendedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendedProjectRepository extends JpaRepository<RecommendedProject, UUID> {

    List<RecommendedProject> findByStudentId(UUID studentId);

    void deleteByStudentId(UUID studentId);
}
