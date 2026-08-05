package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.SkillGapReport;
import com.acea.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillGapReportRepository extends JpaRepository<SkillGapReport, UUID> {

    Optional<SkillGapReport> findTopByStudentOrderByCreatedAtDesc(User student);

    List<SkillGapReport> findAllByStudentOrderByCreatedAtDesc(User student);

    Optional<SkillGapReport> findByStudentAndCareerId(User student, String careerId);

    Optional<SkillGapReport> findByIdAndStudent(UUID id, User student);

    void deleteAllByStudent(User student);
}
