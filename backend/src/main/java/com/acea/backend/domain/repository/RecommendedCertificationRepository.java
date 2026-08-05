package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RecommendedCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendedCertificationRepository extends JpaRepository<RecommendedCertification, UUID> {

    List<RecommendedCertification> findByStudentId(UUID studentId);

    void deleteByStudentId(UUID studentId);
}
