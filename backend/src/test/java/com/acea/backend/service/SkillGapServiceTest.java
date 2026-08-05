package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.skillgap.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.service.impl.SkillGapServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillGapServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private AssessmentResultRepository assessmentResultRepository;

    @Mock
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @Mock
    private RoadmapRepository roadmapRepository;

    @Mock
    private SkillGapReportRepository skillGapReportRepository;

    @Mock
    private SkillGapRepository skillGapRepository;

    @Mock
    private ImprovementRecommendationRepository improvementRecommendationRepository;

    @Mock
    private SkillGapAiClientService skillGapAiClientService;

    @InjectMocks
    private SkillGapServiceImpl skillGapService;

    private User testUser;
    private StudentProfile testProfile;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("student@test.com")
                .role(UserRole.ROLE_STUDENT)
                .build();

        testProfile = StudentProfile.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .profileCompletionPct(85)
                .skills(new ArrayList<>())
                .certifications(new ArrayList<>())
                .projects(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should throw BadRequestException if profile completion < 50%")
    void testIncompleteProfileValidation() {
        testProfile.setProfileCompletionPct(30);
        when(studentProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));

        SkillGapAnalyzeRequest request = SkillGapAnalyzeRequest.builder()
                .targetCareer("Software Engineer")
                .build();

        assertThrows(BadRequestException.class, () -> skillGapService.analyzeSkillGap(testUser, request));
    }

    @Test
    @DisplayName("Should successfully analyze skill gap when profile is valid")
    void testAnalyzeSkillGapSuccess() {
        when(studentProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testProfile));
        when(assessmentResultRepository.findTopByStudentOrderByCompletedAtDesc(testUser)).thenReturn(Optional.empty());
        when(resumeAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(testUser.getId())).thenReturn(Optional.empty());
        when(roadmapRepository.findTopByStudentOrderByCreatedAtDesc(testUser)).thenReturn(Optional.empty());

        SkillGapAiResponse aiResponse = SkillGapAiResponse.builder()
                .targetCareer("Software Engineer")
                .overallReadinessScore(BigDecimal.valueOf(78.50))
                .overallGapPercentage(BigDecimal.valueOf(21.50))
                .estimatedJobReadinessMonths(3)
                .readinessBreakdown(ReadinessBreakdownDto.builder()
                        .overallReadinessScore(BigDecimal.valueOf(78.50))
                        .technicalReadiness(BigDecimal.valueOf(80.00))
                        .softSkillReadiness(BigDecimal.valueOf(85.00))
                        .projectReadiness(BigDecimal.valueOf(70.00))
                        .interviewReadiness(BigDecimal.valueOf(75.00))
                        .resumeReadiness(BigDecimal.valueOf(80.00))
                        .codingReadiness(BigDecimal.valueOf(75.00))
                        .build())
                .skillGaps(List.of(
                        SkillGapDto.builder()
                                .skillName("System Design")
                                .requiredLevel("ADVANCED")
                                .currentLevel("BEGINNER")
                                .gapPercentage(BigDecimal.valueOf(60.00))
                                .priority("HIGH")
                                .estimatedLearningHours(40)
                                .status("MISSING")
                                .category("TECHNICAL")
                                .build()
                ))
                .improvementRecommendations(List.of(
                        ImprovementRecommendationDto.builder()
                                .title("System Design Course")
                                .description("High priority system design tutorial")
                                .recommendationType("COURSE")
                                .priority("HIGH")
                                .estimatedDuration("20 Hours")
                                .build()
                ))
                .build();

        when(skillGapAiClientService.analyze(any(SkillGapAiRequest.class))).thenReturn(aiResponse);

        SkillGapReport mockSavedReport = SkillGapReport.builder()
                .id(UUID.randomUUID())
                .student(testUser)
                .careerId("software-engineer")
                .targetCareer("Software Engineer")
                .overallReadinessScore(BigDecimal.valueOf(78.50))
                .overallGapPercentage(BigDecimal.valueOf(21.50))
                .estimatedJobReadinessMonths(3)
                .technicalReadiness(BigDecimal.valueOf(80.00))
                .skillGaps(new ArrayList<>())
                .build();

        when(skillGapReportRepository.save(any(SkillGapReport.class))).thenReturn(mockSavedReport);
        when(skillGapRepository.save(any(SkillGap.class))).thenAnswer(i -> {
            SkillGap sg = i.getArgument(0);
            sg.setId(UUID.randomUUID());
            sg.setImprovementRecommendations(new ArrayList<>());
            return sg;
        });

        SkillGapAnalyzeRequest request = SkillGapAnalyzeRequest.builder()
                .targetCareer("Software Engineer")
                .build();

        SkillGapReportResponse response = skillGapService.analyzeSkillGap(testUser, request);

        assertNotNull(response);
        assertEquals("Software Engineer", response.getTargetCareer());
        assertEquals(BigDecimal.valueOf(78.50), response.getOverallReadinessScore());
        verify(skillGapReportRepository, times(1)).save(any(SkillGapReport.class));
    }
}
