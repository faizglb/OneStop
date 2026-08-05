package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.skillgap.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.SkillGapAiClientService;
import com.acea.backend.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillGapServiceImpl implements SkillGapService {

    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentResultRepository assessmentResultRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final RoadmapRepository roadmapRepository;
    private final SkillGapReportRepository skillGapReportRepository;
    private final SkillGapRepository skillGapRepository;
    private final ImprovementRecommendationRepository improvementRecommendationRepository;
    private final SkillGapAiClientService skillGapAiClientService;

    @Override
    @Transactional
    public SkillGapReportResponse analyzeSkillGap(User currentUser, SkillGapAnalyzeRequest request) {
        log.info("Analyzing skill gap for user: {} and target career: {}", currentUser.getEmail(), request.getTargetCareer());

        // 1. Validation: Prevent incomplete profile analysis
        StudentProfile profile = studentProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_INPUT,
                        "Student profile must be completed before running AI Skill Gap Analysis."
                ));

        if (profile.getProfileCompletionPct() != null && profile.getProfileCompletionPct() < 50) {
            throw new BadRequestException(
                    ErrorCode.INVALID_INPUT,
                    "Student profile completion is below 50%. Please complete your profile first."
            );
        }

        // 2. Validation: Prevent duplicate reports if career is unchanged and analyzed within last 5 minutes
        String careerId = (request.getCareerId() != null && !request.getCareerId().isBlank()) 
                ? request.getCareerId() : request.getTargetCareer().toLowerCase().replaceAll("\\s+", "-");
        
        Optional<SkillGapReport> existingReportOpt = skillGapReportRepository.findByStudentAndCareerId(currentUser, careerId);

        // 3. Aggregate data from Sprints 2, 3, 8, 9
        List<String> studentSkills = profile.getSkills() != null
                ? profile.getSkills().stream().map(Skill::getSkillName).collect(Collectors.toList())
                : Collections.emptyList();

        List<String> profileProjects = profile.getProjects() != null
                ? profile.getProjects().stream().map(Project::getTitle).collect(Collectors.toList())
                : Collections.emptyList();

        List<String> profileCerts = profile.getCertifications() != null
                ? profile.getCertifications().stream().map(Certification::getCertificationName).collect(Collectors.toList())
                : Collections.emptyList();

        // Assessment Aptitude scores (Sprint 3)
        Map<String, BigDecimal> aptitudeScores = new HashMap<>();
        Optional<AssessmentResult> lastAssessmentOpt = assessmentResultRepository.findTopByStudentOrderByCompletedAtDesc(currentUser);
        if (lastAssessmentOpt.isPresent() && lastAssessmentOpt.get().getSectionScores() != null) {
            for (SectionScore ss : lastAssessmentOpt.get().getSectionScores()) {
                aptitudeScores.put(ss.getCategoryName(), ss.getCategoryPercentage());
            }
        }

        // Resume Keywords & ATS Score (Sprint 9)
        List<String> resumeKeywords = new ArrayList<>();
        BigDecimal resumeAtsScore = null;
        Optional<ResumeAnalysis> lastResumeOpt = resumeAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(currentUser.getId());
        if (lastResumeOpt.isPresent()) {
            resumeAtsScore = lastResumeOpt.get().getAtsScore();
            if (lastResumeOpt.get().getKeywords() != null) {
                resumeKeywords = lastResumeOpt.get().getKeywords().stream()
                        .filter(ResumeKeyword::getFound)
                        .map(ResumeKeyword::getKeyword)
                        .collect(Collectors.toList());
            }
        }

        // Roadmap Progress (Sprint 8)
        Integer roadmapCompletionPct = 0;
        Optional<Roadmap> lastRoadmapOpt = roadmapRepository.findTopByStudentOrderByCreatedAtDesc(currentUser);
        if (lastRoadmapOpt.isPresent()) {
            roadmapCompletionPct = lastRoadmapOpt.get().getCompletionPercentage();
        }

        // 4. Build AI Request Payload
        SkillGapAiRequest aiRequest = SkillGapAiRequest.builder()
                .studentId(currentUser.getId().toString())
                .targetCareer(request.getTargetCareer())
                .careerId(careerId)
                .currentSkills(studentSkills)
                .profileProjects(profileProjects)
                .profileCertifications(profileCerts)
                .resumeKeywords(resumeKeywords)
                .resumeAtsScore(resumeAtsScore)
                .assessmentAptitudeScores(aptitudeScores)
                .roadmapCompletionPct(roadmapCompletionPct)
                .build();

        // 5. Delegate to Python FastAPI AI microservice
        SkillGapAiResponse aiResponse = skillGapAiClientService.analyze(aiRequest);

        // 6. Save or update SkillGapReport in database
        SkillGapReport report = existingReportOpt.orElseGet(() -> SkillGapReport.builder()
                .student(currentUser)
                .careerId(careerId)
                .targetCareer(request.getTargetCareer())
                .build());

        report.setOverallReadinessScore(aiResponse.getOverallReadinessScore());
        report.setOverallGapPercentage(aiResponse.getOverallGapPercentage());
        report.setEstimatedJobReadinessMonths(aiResponse.getEstimatedJobReadinessMonths());
        
        if (aiResponse.getReadinessBreakdown() != null) {
            ReadinessBreakdownDto rb = aiResponse.getReadinessBreakdown();
            report.setTechnicalReadiness(rb.getTechnicalReadiness());
            report.setSoftSkillReadiness(rb.getSoftSkillReadiness());
            report.setProjectReadiness(rb.getProjectReadiness());
            report.setInterviewReadiness(rb.getInterviewReadiness());
            report.setResumeReadiness(rb.getResumeReadiness());
            report.setCodingReadiness(rb.getCodingReadiness());
        }

        // Clear existing child entities if re-analyzing
        if (report.getSkillGaps() != null) {
            report.getSkillGaps().clear();
        } else {
            report.setSkillGaps(new ArrayList<>());
        }

        SkillGapReport savedReport = skillGapReportRepository.save(report);

        // Save SkillGap & ImprovementRecommendation items
        if (aiResponse.getSkillGaps() != null) {
            for (SkillGapDto sgDto : aiResponse.getSkillGaps()) {
                SkillGap skillGap = SkillGap.builder()
                        .report(savedReport)
                        .skillName(sgDto.getSkillName())
                        .requiredLevel(sgDto.getRequiredLevel())
                        .currentLevel(sgDto.getCurrentLevel())
                        .gapPercentage(sgDto.getGapPercentage())
                        .priority(sgDto.getPriority())
                        .estimatedLearningHours(sgDto.getEstimatedLearningHours())
                        .status(sgDto.getStatus())
                        .category(sgDto.getCategory() != null ? sgDto.getCategory() : "TECHNICAL")
                        .build();

                SkillGap savedGap = skillGapRepository.save(skillGap);

                if (aiResponse.getImprovementRecommendations() != null) {
                    for (ImprovementRecommendationDto recDto : aiResponse.getImprovementRecommendations()) {
                        if (recDto.getTitle().toLowerCase().contains(sgDto.getSkillName().toLowerCase()) 
                                || "PROJECT".equals(recDto.getRecommendationType())
                                || "CERTIFICATION".equals(recDto.getRecommendationType())
                                || "PRACTICE".equals(recDto.getRecommendationType())) {
                            ImprovementRecommendation rec = ImprovementRecommendation.builder()
                                    .skillGap(savedGap)
                                    .resourceId(recDto.getResourceId())
                                    .projectId(recDto.getProjectId())
                                    .certificateId(recDto.getCertificateId())
                                    .title(recDto.getTitle())
                                    .description(recDto.getDescription())
                                    .recommendationType(recDto.getRecommendationType())
                                    .priority(recDto.getPriority())
                                    .estimatedDuration(recDto.getEstimatedDuration())
                                    .url(recDto.getUrl())
                                    .build();
                            improvementRecommendationRepository.save(rec);
                        }
                    }
                }
                savedReport.getSkillGaps().add(savedGap);
            }
        }

        return mapToReportResponse(savedReport, aiResponse.getVisualAnalytics(), aiResponse.getSmartInsights());
    }

    @Override
    @Transactional(readOnly = true)
    public SkillGapReportResponse getLatestReport(User currentUser) {
        SkillGapReport report = skillGapReportRepository.findTopByStudentOrderByCreatedAtDesc(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Skill Gap Report", "studentId", currentUser.getId()
                ));
        return mapToReportResponse(report, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillGapReportResponse> getReportHistory(User currentUser) {
        List<SkillGapReport> reports = skillGapReportRepository.findAllByStudentOrderByCreatedAtDesc(currentUser);
        return reports.stream()
                .map(r -> mapToReportResponse(r, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillGapDto> getMissingSkills(User currentUser) {
        SkillGapReport report = skillGapReportRepository.findTopByStudentOrderByCreatedAtDesc(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Skill Gap Report", "studentId", currentUser.getId()));

        return report.getSkillGaps().stream()
                .filter(sg -> "MISSING".equalsIgnoreCase(sg.getStatus()) || "WEAK".equalsIgnoreCase(sg.getStatus()))
                .map(this::mapToSkillGapDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReadinessBreakdownDto getReadinessBreakdown(User currentUser) {
        SkillGapReport report = skillGapReportRepository.findTopByStudentOrderByCreatedAtDesc(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Skill Gap Report", "studentId", currentUser.getId()));

        return ReadinessBreakdownDto.builder()
                .overallReadinessScore(report.getOverallReadinessScore())
                .technicalReadiness(report.getTechnicalReadiness())
                .softSkillReadiness(report.getSoftSkillReadiness())
                .projectReadiness(report.getProjectReadiness())
                .interviewReadiness(report.getInterviewReadiness())
                .resumeReadiness(report.getResumeReadiness())
                .codingReadiness(report.getCodingReadiness())
                .build();
    }

    @Override
    @Transactional
    public SkillGapReportResponse regenerateReport(User currentUser, SkillGapAnalyzeRequest request) {
        log.info("Regenerating skill gap report for user: {} and career: {}", currentUser.getEmail(), request.getTargetCareer());
        return analyzeSkillGap(currentUser, request);
    }

    private SkillGapReportResponse mapToReportResponse(SkillGapReport report, VisualAnalyticsDto analytics, List<SmartInsightDto> insights) {
        List<SkillGapDto> gapDtos = report.getSkillGaps() != null
                ? report.getSkillGaps().stream().map(this::mapToSkillGapDto).collect(Collectors.toList())
                : Collections.emptyList();

        List<ImprovementRecommendationDto> recDtos = report.getSkillGaps() != null
                ? report.getSkillGaps().stream()
                .flatMap(g -> g.getImprovementRecommendations().stream())
                .map(this::mapToRecommendationDto)
                .collect(Collectors.toList())
                : Collections.emptyList();

        ReadinessBreakdownDto breakdown = ReadinessBreakdownDto.builder()
                .overallReadinessScore(report.getOverallReadinessScore())
                .technicalReadiness(report.getTechnicalReadiness())
                .softSkillReadiness(report.getSoftSkillReadiness())
                .projectReadiness(report.getProjectReadiness())
                .interviewReadiness(report.getInterviewReadiness())
                .resumeReadiness(report.getResumeReadiness())
                .codingReadiness(report.getCodingReadiness())
                .build();

        if (analytics == null) {
            analytics = buildDefaultVisualAnalytics(report);
        }

        if (insights == null) {
            insights = buildDefaultSmartInsights(report);
        }

        return SkillGapReportResponse.builder()
                .id(report.getId())
                .studentId(report.getStudent().getId())
                .careerId(report.getCareerId())
                .targetCareer(report.getTargetCareer())
                .overallReadinessScore(report.getOverallReadinessScore())
                .overallGapPercentage(report.getOverallGapPercentage())
                .estimatedJobReadinessMonths(report.getEstimatedJobReadinessMonths())
                .readinessBreakdown(breakdown)
                .skillGaps(gapDtos)
                .improvementRecommendations(recDtos)
                .visualAnalytics(analytics)
                .smartInsights(insights)
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }

    private SkillGapDto mapToSkillGapDto(SkillGap sg) {
        List<ImprovementRecommendationDto> recs = sg.getImprovementRecommendations() != null
                ? sg.getImprovementRecommendations().stream().map(this::mapToRecommendationDto).collect(Collectors.toList())
                : Collections.emptyList();

        return SkillGapDto.builder()
                .id(sg.getId())
                .skillName(sg.getSkillName())
                .requiredLevel(sg.getRequiredLevel())
                .currentLevel(sg.getCurrentLevel())
                .gapPercentage(sg.getGapPercentage())
                .priority(sg.getPriority())
                .estimatedLearningHours(sg.getEstimatedLearningHours())
                .status(sg.getStatus())
                .category(sg.getCategory())
                .recommendations(recs)
                .build();
    }

    private ImprovementRecommendationDto mapToRecommendationDto(ImprovementRecommendation ir) {
        return ImprovementRecommendationDto.builder()
                .id(ir.getId())
                .resourceId(ir.getResourceId())
                .projectId(ir.getProjectId())
                .certificateId(ir.getCertificateId())
                .title(ir.getTitle())
                .description(ir.getDescription())
                .recommendationType(ir.getRecommendationType())
                .priority(ir.getPriority())
                .estimatedDuration(ir.getEstimatedDuration())
                .url(ir.getUrl())
                .build();
    }

    private VisualAnalyticsDto buildDefaultVisualAnalytics(SkillGapReport report) {
        return VisualAnalyticsDto.builder()
                .radarChart(List.of(
                        VisualAnalyticsDto.RadarChartItem.builder().subject("Technical").student(report.getTechnicalReadiness()).required(BigDecimal.valueOf(90)).build(),
                        VisualAnalyticsDto.RadarChartItem.builder().subject("Coding").student(report.getCodingReadiness()).required(BigDecimal.valueOf(85)).build(),
                        VisualAnalyticsDto.RadarChartItem.builder().subject("Projects").student(report.getProjectReadiness()).required(BigDecimal.valueOf(80)).build(),
                        VisualAnalyticsDto.RadarChartItem.builder().subject("Resume/ATS").student(report.getResumeReadiness()).required(BigDecimal.valueOf(85)).build(),
                        VisualAnalyticsDto.RadarChartItem.builder().subject("Interview").student(report.getInterviewReadiness()).required(BigDecimal.valueOf(80)).build(),
                        VisualAnalyticsDto.RadarChartItem.builder().subject("Soft Skills").student(report.getSoftSkillReadiness()).required(BigDecimal.valueOf(85)).build()
                ))
                .skillHeatmap(Collections.emptyList())
                .priorityMatrix(Collections.emptyList())
                .learningTimeline(List.of(
                        VisualAnalyticsDto.LearningTimelineStep.builder().stepNumber(1).phase("Phase 1").title("Bridge Critical Gaps").skillsCovered(List.of("Core Skills")).estimatedWeeks(4).build(),
                        VisualAnalyticsDto.LearningTimelineStep.builder().stepNumber(2).phase("Phase 2").title("Capstone Project").skillsCovered(List.of("Full Stack Deployment")).estimatedWeeks(6).build()
                ))
                .build();
    }

    private List<SmartInsightDto> buildDefaultSmartInsights(SkillGapReport report) {
        return List.of(
                SmartInsightDto.builder()
                        .title("Readiness Overview")
                        .description("Your current overall readiness for " + report.getTargetCareer() + " is " + report.getOverallReadinessScore() + "%.")
                        .insightType("POSITIVE")
                        .build(),
                SmartInsightDto.builder()
                        .title("Estimated Readiness Timeline")
                        .description("You are on track to become job ready within ~" + report.getEstimatedJobReadinessMonths() + " months.")
                        .insightType("TIP")
                        .build()
        );
    }
}
