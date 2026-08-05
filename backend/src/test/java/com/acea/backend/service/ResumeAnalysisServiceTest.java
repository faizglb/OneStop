package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.resume.*;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.impl.ResumeAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResumeAnalysisService — Unit Tests")
class ResumeAnalysisServiceTest {

    @Mock private ResumeAnalysisRepository       analysisRepository;
    @Mock private ResumeScoreBreakdownRepository  scoreRepository;
    @Mock private ResumeSectionRepository         sectionRepository;
    @Mock private ResumeKeywordRepository         keywordRepository;
    @Mock private ResumeSuggestionRepository      suggestionRepository;
    @Mock private StudentProfileRepository        profileRepository;
    @Mock private SkillRepository                 skillRepository;
    @Mock private GoalRepository                  goalRepository;
    @Mock private ResumeAiClientService           aiClient;

    @InjectMocks private ResumeAnalysisServiceImpl analysisService;

    private UUID userId;
    private UUID analysisId;
    private ResumeAnalysis analysis;
    private ResumeAiResponse aiResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        analysisId = UUID.randomUUID();

        analysis = ResumeAnalysis.builder()
                .id(analysisId)
                .userId(userId)
                .fileName("john_doe_resume.pdf")
                .fileUrl("http://res.cloudinary.com/demo/raw/john_doe_resume.pdf")
                .fileType("PDF")
                .status("COMPLETED")
                .atsScore(BigDecimal.valueOf(82.50))
                .overallScore(BigDecimal.valueOf(85.00))
                .targetCareer("Software Engineer")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .sections(new ArrayList<>())
                .keywords(new ArrayList<>())
                .suggestions(new ArrayList<>())
                .build();

        // Sub-models for AI response mock
        ResumeAiResponse.ScoreBreakdownDto scores = new ResumeAiResponse.ScoreBreakdownDto(
                BigDecimal.valueOf(85.00), BigDecimal.valueOf(90.00), BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(75.00), BigDecimal.valueOf(85.00), BigDecimal.valueOf(90.00)
        );

        List<ResumeAiResponse.SectionDto> sections = List.of(
                new ResumeAiResponse.SectionDto("CONTACT", true, "HIGH", "John Doe - contact@email.com"),
                new ResumeAiResponse.SectionDto("EXPERIENCE", true, "HIGH", "Developer Intern at Tech Corp")
        );

        List<ResumeAiResponse.KeywordDto> keywords = List.of(
                new ResumeAiResponse.KeywordDto("Java", "Technical Skill", true, 3, "HIGH"),
                new ResumeAiResponse.KeywordDto("Docker", "DevOps", false, 0, "MEDIUM")
        );

        List<ResumeAiResponse.SuggestionDto> suggestions = List.of(
                new ResumeAiResponse.SuggestionDto("KEYWORDS", "HIGH", "Missing Docker keyword", "Add Docker", "Used Docker for deployment")
        );

        aiResponse = new ResumeAiResponse(
                BigDecimal.valueOf(82.50), BigDecimal.valueOf(85.00), 450, 1,
                scores, sections, keywords, suggestions
        );
    }

    @Test
    @DisplayName("analyzeResume — triggers analysis flow, saves results, and returns mapped response")
    void analyzeResume_savesAndReturns() {
        // Stub profile & repo methods
        given(profileRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(analysisRepository.save(any(ResumeAnalysis.class))).willAnswer(inv -> {
            ResumeAnalysis entity = inv.getArgument(0);
            entity.setId(analysisId);
            return entity;
        });
        given(aiClient.analyze(any(ResumeAiRequest.class))).willReturn(aiResponse);

        ResumeAnalysisRequest req = new ResumeAnalysisRequest(
                "http://res.cloudinary.com/demo/raw/john_doe_resume.pdf",
                "john_doe_resume.pdf",
                "PDF",
                "Software Engineer"
        );

        ResumeAnalysisResponse res = analysisService.analyzeResume(userId, req);

        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(analysisId);
        assertThat(res.atsScore()).isEqualTo(BigDecimal.valueOf(82.50));
        assertThat(res.status()).isEqualTo("COMPLETED");

        verify(analysisRepository, org.mockito.Mockito.times(2)).save(any(ResumeAnalysis.class));
        verify(scoreRepository).save(any(ResumeScoreBreakdown.class));
        verify(sectionRepository, org.mockito.Mockito.times(2)).save(any(ResumeSection.class));
        verify(keywordRepository, org.mockito.Mockito.times(2)).save(any(ResumeKeyword.class));
        verify(suggestionRepository, org.mockito.Mockito.times(1)).save(any(ResumeSuggestion.class));
    }

    @Test
    @DisplayName("getAnalysisHistory — returns paginated summaries of past analyses")
    void getAnalysisHistory_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResumeAnalysis> page = new PageImpl<>(List.of(analysis), pageable, 1);
        given(analysisRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)).willReturn(page);

        Page<ResumeAnalysisSummaryResponse> history = analysisService.getAnalysisHistory(userId, pageable);

        assertThat(history).isNotNull();
        assertThat(history.getContent()).hasSize(1);
        assertThat(history.getContent().get(0).id()).isEqualTo(analysisId);
        assertThat(history.getContent().get(0).atsScore()).isEqualTo(BigDecimal.valueOf(82.50));
    }

    @Test
    @DisplayName("getAnalysisById — retrieves a specific record if owner match")
    void getAnalysisById_returnsRecord() {
        given(analysisRepository.findByIdAndUserId(analysisId, userId)).willReturn(Optional.of(analysis));

        ResumeAnalysisResponse res = analysisService.getAnalysisById(userId, analysisId);

        assertThat(res).isNotNull();
        assertThat(res.id()).isEqualTo(analysisId);
        assertThat(res.fileName()).isEqualTo("john_doe_resume.pdf");
    }

    @Test
    @DisplayName("getAnalysisById — throws ResourceNotFoundException if access denied or not found")
    void getAnalysisById_throwsIfNotFound() {
        given(analysisRepository.findByIdAndUserId(analysisId, userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> analysisService.getAnalysisById(userId, analysisId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteAnalysis — successfully deletes analysis record")
    void deleteAnalysis_deletes() {
        given(analysisRepository.findByIdAndUserId(analysisId, userId)).willReturn(Optional.of(analysis));

        analysisService.deleteAnalysis(userId, analysisId);

        verify(analysisRepository).delete(analysis);
    }
}
