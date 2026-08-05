package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RecommendedSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendedSkillRepository extends JpaRepository<RecommendedSkill, UUID> {

    List<RecommendedSkill> findByStudentId(UUID studentId);

    void deleteByStudentId(UUID studentId);
}
