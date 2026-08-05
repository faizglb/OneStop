package com.acea.backend.service;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.recommendation.*;
import com.acea.backend.exception.*;
import com.acea.backend.service.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendationService — Unit Tests")
class RecommendationServiceTest {

    @Mock private CareerRecommendationRepository careerRecommendationRepository;
    @Mock private RecommendedSkillRepository recommendedSkillRepository;
    @Mock private RecommendedCertificationRepository recommendedCertificationRepository;
    @Mock private RecommendedProjectRepository recommendedProjectRepository;
    @Mock private StudentProfileRepository studentProfileRepository;
    @Mock private AssessmentResultRepository resultRepository;
    @Mock private SectionScoreRepository sectionScoreRepository;
    @Mock private TraitScoreRepository traitScoreRepository;
    @Mock private UserRepository userRepository;
    @Mock private AppProperties appProperties;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private UUID studentId;
    private User student;
    private StudentProfile profile;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        student = User.builder().id(studentId).email("student@acea.com").build();
        profile = StudentProfile.builder()
                .id(UUID.randomUUID())
                .user(student)
                .firstName("John")
                .lastName("Doe")
                .educationLevel("Bachelor's")
                .currentCourse("B.Tech")
                .branch("CS")
                .currentYear(3)
                .currentCgpa(BigDecimal.valueOf(8.5))
                .build();
    }

    @Test
    @DisplayName("generateRecommendations — throws BadRequestException if assessments not completed")
    void generateRecommendations_throwsIfAssessmentsNotCompleted() {
        given(studentProfileRepository.findByUserId(studentId)).willReturn(Optional.of(profile));
        // Return empty list of completed assessments
        given(resultRepository.findByStudentIdAndStatusOrderByCompletedAtDesc(studentId, ResultStatus.COMPLETED))
                .willReturn(List.of());

        assertThatThrownBy(() -> recommendationService.generateRecommendations(studentId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You must complete all three assessments");
    }

    @Test
    @DisplayName("toggleBookmark — toggles state successfully")
    void toggleBookmark_togglesBookmarkState() {
        UUID recId = UUID.randomUUID();
        CareerRecommendation rec = CareerRecommendation.builder()
                .id(recId)
                .student(student)
                .careerName("Software Engineer")
                .isBookmarked(false)
                .build();

        given(careerRecommendationRepository.findById(recId)).willReturn(Optional.of(rec));
        given(careerRecommendationRepository.save(any(CareerRecommendation.class))).willAnswer(inv -> inv.getArgument(0));

        RecommendationResponse response = recommendationService.toggleBookmark(studentId, recId);

        assertThat(response.isBookmarked()).isTrue();
        verify(careerRecommendationRepository).save(rec);
    }

    @Test
    @DisplayName("toggleBookmark — throws Forbidden/Unauthorized exception for other users")
    void toggleBookmark_throwsForOtherUser() {
        UUID recId = UUID.randomUUID();
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        CareerRecommendation rec = CareerRecommendation.builder()
                .id(recId)
                .student(otherUser) // Owned by other user
                .careerName("UX Designer")
                .isBookmarked(false)
                .build();

        given(careerRecommendationRepository.findById(recId)).willReturn(Optional.of(rec));

        assertThatThrownBy(() -> recommendationService.toggleBookmark(studentId, recId))
                .isInstanceOf(BadRequestException.class);
    }
}
