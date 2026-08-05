package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.SkillGap;
import com.acea.backend.domain.entity.SkillGapReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillGapRepository extends JpaRepository<SkillGap, UUID> {

    List<SkillGap> findAllByReport(SkillGapReport report);

    List<SkillGap> findAllByReportAndStatus(SkillGapReport report, String status);
}
