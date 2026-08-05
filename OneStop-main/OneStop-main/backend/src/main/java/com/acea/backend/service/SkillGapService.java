package com.acea.backend.service;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.skillgap.SkillGapAnalyzeRequest;
import com.acea.backend.dto.skillgap.SkillGapDto;
import com.acea.backend.dto.skillgap.SkillGapReportResponse;
import com.acea.backend.dto.skillgap.ReadinessBreakdownDto;

import java.util.List;

public interface SkillGapService {

    SkillGapReportResponse analyzeSkillGap(User currentUser, SkillGapAnalyzeRequest request);

    SkillGapReportResponse getLatestReport(User currentUser);

    List<SkillGapReportResponse> getReportHistory(User currentUser);

    List<SkillGapDto> getMissingSkills(User currentUser);

    ReadinessBreakdownDto getReadinessBreakdown(User currentUser);

    SkillGapReportResponse regenerateReport(User currentUser, SkillGapAnalyzeRequest request);
}
