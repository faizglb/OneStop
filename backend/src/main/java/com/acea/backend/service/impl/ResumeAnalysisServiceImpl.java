package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.resume.*;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.ResumeAiClientService;
import com.acea.backend.service.ResumeAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private final ResumeAnalysisRepository       analysisRepository;
    private final ResumeScoreBreakdownRepository  scoreRepository;
    private final ResumeSectionRepository         sectionRepository;
    private final ResumeKeywordRepository         keywordRepository;
    private final ResumeSuggestionRepository      suggestionRepository;
    private final StudentProfileRepository        profileRepository;
    private final SkillRepository                 skillRepository;
    private final GoalRepository                  goalRepository;
    private final ResumeAiClientService           aiClient;

    // ── Analyze ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ResumeAnalysisResponse analyzeResume(UUID userId, ResumeAnalysisRequest request) {
        log.info("Starting resume analysis for userId={}, file={}", userId, request.fileName());

        // Create initial PENDING record
        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .userId(userId)
                .fileName(request.fileName())
                .fileUrl(request.fileUrl())
                .fileType(request.fileType())
                .status("PROCESSING")
                .targetCareer(request.targetCareer())
                .build();
        analysis = analysisRepository.save(analysis);

        try {
            // Gather student context for context-aware AI feedback
            StudentProfile profile = profileRepository.findByUserId(userId).orElse(null);
            UUID profileId = profile != null ? profile.getId() : null;

            List<String> skills = List.of();
            List<String> goals = List.of();
            if (profileId != null) {
                skills = skillRepository.findByStudentProfileId(profileId)
                        .stream().map(Skill::getSkillName).toList();
                goals = goalRepository.findByStudentProfileId(profileId)
                        .stream().map(Goal::getGoalTitle).toList();
            }

            String educationLevel = profile != null && profile.getEducationLevel() != null
                    ? profile.getEducationLevel() : "UNDERGRADUATE";

            String branch = profile != null && profile.getBranch() != null
                    ? profile.getBranch() : "Computer Science";

            // Build AI request payload
            ResumeAiRequest aiRequest = new ResumeAiRequest(
                    "",               // resumeText — AI service downloads and extracts from fileUrl
                    request.fileName(),
                    request.targetCareer(),
                    skills,
                    goals,
                    branch,
                    educationLevel
            );

            // NOTE: We pass the fileUrl separately as a custom header-like trick;
            // for simplicity the Python service is told to treat fileUrl as input context.
            // In production, text extraction can happen in Spring using Tika, then passed as resumeText.
            // For now, we build a combined request including fileUrl as targetCareer context cue.
            ResumeAiRequest aiRequestWithUrl = new ResumeAiRequest(
                    request.fileUrl(),   // Python service reads URL as context
                    request.fileName(),
                    request.targetCareer() != null ? request.targetCareer() : "Software Engineer",
                    skills,
                    goals,
                    branch,
                    educationLevel
            );

            // Call Python AI service
            ResumeAiResponse aiResponse = aiClient.analyze(aiRequestWithUrl);

            // Persist score breakdown
            ResumeScoreBreakdown breakdown = ResumeScoreBreakdown.builder()
                    .analysis(analysis)
                    .keywordScore(safe(aiResponse.scoreBreakdown() != null ? aiResponse.scoreBreakdown().keywordScore() : null))
                    .sectionScore(safe(aiResponse.scoreBreakdown() != null ? aiResponse.scoreBreakdown().sectionScore() : null))
                    .formattingScore(safe(aiResponse.scoreBreakdown() != null ? aiResponse.scoreBreakdown().formattingScore() : null))
                    .experienceScore(safe(aiResponse.scoreBreakdown() != null ? aiResponse.scoreBreakdown().experienceScore() : null))
                    .educationScore(safe(aiResponse.scoreBreakdown() != null ? aiResponse.scoreBreakdown().educationScore() : null))
                    .grammarScore(safe(aiResponse.scoreBreakdown() != null ? aiResponse.scoreBreakdown().grammarScore() : null))
                    .build();
            scoreRepository.save(breakdown);
            analysis.setScoreBreakdown(breakdown);

            // Persist sections
            if (aiResponse.sections() != null) {
                for (ResumeAiResponse.SectionDto s : aiResponse.sections()) {
                    ResumeSection section = ResumeSection.builder()
                            .analysis(analysis)
                            .sectionName(s.sectionName())
                            .detected(s.detected() != null && s.detected())
                            .quality(s.quality() != null ? s.quality() : "LOW")
                            .contentSnippet(s.contentSnippet())
                            .build();
                    sectionRepository.save(section);
                    analysis.getSections().add(section);
                }
            }

            // Persist keywords
            if (aiResponse.keywords() != null) {
                for (ResumeAiResponse.KeywordDto k : aiResponse.keywords()) {
                    ResumeKeyword keyword = ResumeKeyword.builder()
                            .analysis(analysis)
                            .keyword(k.keyword())
                            .category(k.category())
                            .found(k.found() != null && k.found())
                            .frequency(k.frequency() != null ? k.frequency() : 0)
                            .importance(k.importance() != null ? k.importance() : "MEDIUM")
                            .build();
                    keywordRepository.save(keyword);
                    analysis.getKeywords().add(keyword);
                }
            }

            // Persist suggestions
            if (aiResponse.suggestions() != null) {
                for (ResumeAiResponse.SuggestionDto sug : aiResponse.suggestions()) {
                    ResumeSuggestion suggestion = ResumeSuggestion.builder()
                            .analysis(analysis)
                            .category(sug.category() != null ? sug.category() : "GENERAL")
                            .priority(sug.priority() != null ? sug.priority() : "MEDIUM")
                            .title(sug.title())
                            .description(sug.description())
                            .example(sug.example())
                            .build();
                    suggestionRepository.save(suggestion);
                    analysis.getSuggestions().add(suggestion);
                }
            }

            // Update main analysis record
            analysis.setStatus("COMPLETED");
            analysis.setAtsScore(safe(aiResponse.atsScore()));
            analysis.setOverallScore(safe(aiResponse.overallScore()));
            analysis.setWordCount(aiResponse.wordCount() != null ? aiResponse.wordCount() : 0);
            analysis.setPageCount(aiResponse.pageCount() != null ? aiResponse.pageCount() : 1);
            analysis = analysisRepository.save(analysis);

            log.info("Resume analysis COMPLETED for userId={}, analysisId={}, atsScore={}", userId, analysis.getId(), analysis.getAtsScore());
            return toFullResponse(analysis);

        } catch (Exception e) {
            // Mark as failed without losing the record
            analysis.setStatus("FAILED");
            analysis.setErrorMessage(e.getMessage());
            analysisRepository.save(analysis);
            log.error("Resume analysis FAILED for userId={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    // ── History ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<ResumeAnalysisSummaryResponse> getAnalysisHistory(UUID userId, Pageable pageable) {
        return analysisRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getLatestAnalysis(UUID userId) {
        ResumeAnalysis analysis = analysisRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No resume analysis found. Please upload and analyze a resume first."));
        return toFullResponse(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeAnalysisResponse getAnalysisById(UUID userId, UUID analysisId) {
        ResumeAnalysis analysis = analysisRepository.findByIdAndUserId(analysisId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume analysis not found or access denied."));
        return toFullResponse(analysis);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteAnalysis(UUID userId, UUID analysisId) {
        ResumeAnalysis analysis = analysisRepository.findByIdAndUserId(analysisId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume analysis not found or access denied."));
        analysisRepository.delete(analysis);
        log.info("Deleted resume analysis={} for userId={}", analysisId, userId);
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private ResumeAnalysisResponse toFullResponse(ResumeAnalysis a) {
        ResumeScoreBreakdownResponse scoreDto = null;
        if (a.getScoreBreakdown() != null) {
            ResumeScoreBreakdown sb = a.getScoreBreakdown();
            scoreDto = new ResumeScoreBreakdownResponse(
                    sb.getKeywordScore(), sb.getSectionScore(), sb.getFormattingScore(),
                    sb.getExperienceScore(), sb.getEducationScore(), sb.getGrammarScore()
            );
        }

        List<ResumeSectionResponse> sections = a.getSections().stream()
                .map(s -> new ResumeSectionResponse(s.getSectionName(), s.getDetected(), s.getQuality(), s.getContentSnippet()))
                .toList();

        List<ResumeKeywordResponse> keywords = a.getKeywords().stream()
                .map(k -> new ResumeKeywordResponse(k.getKeyword(), k.getCategory(), k.getFound(), k.getFrequency(), k.getImportance()))
                .toList();

        List<ResumeSuggestionResponse> suggestions = a.getSuggestions().stream()
                .map(s -> new ResumeSuggestionResponse(s.getCategory(), s.getPriority(), s.getTitle(), s.getDescription(), s.getExample()))
                .toList();

        return new ResumeAnalysisResponse(
                a.getId(), a.getFileName(), a.getFileUrl(), a.getFileType(),
                a.getWordCount(), a.getPageCount(), a.getStatus(),
                a.getAtsScore(), a.getOverallScore(), a.getTargetCareer(),
                a.getCreatedAt(), a.getUpdatedAt(),
                scoreDto, sections, keywords, suggestions
        );
    }

    private ResumeAnalysisSummaryResponse toSummaryResponse(ResumeAnalysis a) {
        return new ResumeAnalysisSummaryResponse(
                a.getId(), a.getFileName(), a.getFileType(), a.getStatus(),
                a.getAtsScore(), a.getOverallScore(), a.getTargetCareer(),
                a.getWordCount(), a.getPageCount(), a.getCreatedAt()
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
