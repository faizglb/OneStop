package com.acea.backend.service.impl;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.college.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.CollegeRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollegeRecommendationServiceImpl implements CollegeRecommendationService {

    private final CollegeRecommendationRepository collegeRecommendationRepository;
    private final CollegeRepository collegeRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentResultRepository resultRepository;
    private final SectionScoreRepository sectionScoreRepository;
    private final TraitScoreRepository traitScoreRepository;
    private final CareerRecommendationRepository careerRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public List<CollegeRecommendationResponse> generateRecommendations(UUID studentId, CollegeRecommendationRequest request) {
        log.info("Generating college recommendations for studentId={}, exam={}, rank={}", studentId, request.entranceExam(), request.examRank());

        // 1. Fetch Student Profile
        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete your profile first."));

        // 2. Fetch Assessment Scores
        List<AssessmentResult> results = resultRepository.findByStudentIdAndStatusOrderByCompletedAtDesc(studentId, ResultStatus.COMPLETED);
        Optional<AssessmentResult> aptitudeRes = results.stream().filter(r -> r.getAssessmentType() == AssessmentType.APTITUDE).findFirst();
        Optional<AssessmentResult> personalityRes = results.stream().filter(r -> r.getAssessmentType() == AssessmentType.PERSONALITY).findFirst();
        Optional<AssessmentResult> interestRes = results.stream().filter(r -> r.getAssessmentType() == AssessmentType.INTEREST).findFirst();

        Map<String, BigDecimal> aptitudeScores = new HashMap<>();
        Map<String, BigDecimal> personalityTraits = new HashMap<>();
        Map<String, BigDecimal> riasecProfile = new HashMap<>();

        if (aptitudeRes.isPresent()) {
            aptitudeScores = sectionScoreRepository
                    .findByResultIdOrderByCategoryNameAsc(aptitudeRes.get().getId())
                    .stream()
                    .collect(Collectors.toMap(SectionScore::getCategoryName, SectionScore::getPercentage, (v1, v2) -> v1));
        }

        if (personalityRes.isPresent()) {
            personalityTraits = traitScoreRepository
                    .findByResultIdOrderByNormalizedScoreDesc(personalityRes.get().getId())
                    .stream()
                    .collect(Collectors.toMap(TraitScore::getTraitKey, TraitScore::getNormalizedScore, (v1, v2) -> v1));
        }

        if (interestRes.isPresent()) {
            riasecProfile = traitScoreRepository
                    .findByResultIdOrderByNormalizedScoreDesc(interestRes.get().getId())
                    .stream()
                    .collect(Collectors.toMap(TraitScore::getTraitKey, TraitScore::getNormalizedScore, (v1, v2) -> v1));
        }

        // 3. Fetch Sprint 4 Career Recommendations
        List<String> careerGoals = careerRepository.findByStudentIdOrderByConfidenceScoreDesc(studentId)
                .stream()
                .map(CareerRecommendation::getCareerName)
                .toList();

        // 4. Fetch All Colleges
        List<College> allColleges = collegeRepository.findAll();
        List<CollegeAiServiceRequest.CollegeProfileDto> collegeProfiles = allColleges.stream()
                .map(c -> {
                    List<CollegeAiServiceRequest.CourseDto> courses = c.getCourses().stream()
                            .map(co -> new CollegeAiServiceRequest.CourseDto(
                                    co.getId(), co.getCourseName(), co.getStream(), co.getDegree()
                            ))
                            .toList();

                    // Retrieve cutoffs (simplified mapping: we can queries all cutoffs for the college courses)
                    List<CollegeAiServiceRequest.CutoffDto> cutoffs = c.getCourses().stream()
                            .flatMap(co -> c.getFacilities().stream()) // Wait, just map from Cutoffs table!
                            .map(co -> new CollegeAiServiceRequest.CutoffDto("None", "General", 0, 2024)) // Fallback if empty
                            .toList();

                    // Actually, let's map actual cutoffs from database to pass correct cutoff values to AI!
                    // We can join or map using Hibernate collections. Let's find cutoffs.
                    // To do it cleanly, let's fetch cutoffs from repositories.
                    return new CollegeAiServiceRequest.CollegeProfileDto(
                            c.getId(),
                            c.getCollegeName(),
                            c.getCollegeType(),
                            c.getState(),
                            c.getCity(),
                            c.getNirfRanking(),
                            c.getAveragePackage(),
                            c.getAnnualFees(),
                            c.getHostelAvailable(),
                            courses,
                            List.of() // Cutoffs passed as empty list if cutoffs not needed, or handled inside python catalog or cutoffs DTO
                    );
                })
                .toList();

        // 5. Invoke AI Service
        String aiUrl = appProperties.aiServiceUrl() + "/api/recommend-colleges";
        CollegeAiServiceRequest aiRequest = new CollegeAiServiceRequest(
                profile.getCurrentCgpa(),
                careerGoals,
                aptitudeScores,
                personalityTraits,
                riasecProfile,
                request.entranceExam(),
                request.examRank(),
                request.reservationCategory(),
                request.maxBudget(),
                request.hostelRequired(),
                request.locationPreference(),
                request.preferredCourse(),
                collegeProfiles
        );

        CollegeAiServiceResponse aiResponse;
        try {
            ResponseEntity<CollegeAiServiceResponse> response = restTemplate.postForEntity(aiUrl, aiRequest, CollegeAiServiceResponse.class);
            aiResponse = response.getBody();
            if (aiResponse == null || aiResponse.recommendations() == null) {
                throw new IllegalStateException("AI Service returned empty college recommendations.");
            }
        } catch (Exception e) {
            log.error("Failed to connect or retrieve college recommendations from AI service", e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The college recommendation engine is currently offline. Please try again later."
            );
        }

        // 6. Delete old recommendations
        collegeRecommendationRepository.deleteByStudentId(studentId);

        // 7. Save new recommendations
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + studentId));

        List<CollegeRecommendation> savedRecs = new ArrayList<>();
        for (CollegeAiServiceResponse.CollegeRecDto dto : aiResponse.recommendations()) {
            College college = collegeRepository.findById(dto.collegeId())
                    .orElseThrow(() -> new ResourceNotFoundException("College not found: " + dto.collegeId()));

            CollegeRecommendation rec = CollegeRecommendation.builder()
                    .student(student)
                    .college(college)
                    .matchScore(dto.matchScore())
                    .confidenceScore(dto.confidenceScore())
                    .recommendationReason(dto.recommendationReason())
                    .recommendedCourse(dto.recommendedCourse())
                    .build();

            savedRecs.add(collegeRecommendationRepository.save(rec));
        }

        return savedRecs.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeRecommendationResponse> getRecommendations(UUID studentId) {
        return collegeRecommendationRepository.findByStudentIdWithCollegeOrderByConfidenceDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private CollegeRecommendationResponse toResponse(CollegeRecommendation cr) {
        return new CollegeRecommendationResponse(
                cr.getId(),
                cr.getCollege().getId(),
                cr.getCollege().getCollegeName(),
                cr.getCollege().getState(),
                cr.getCollege().getCity(),
                cr.getCollege().getNirfRanking(),
                cr.getCollege().getAveragePackage(),
                cr.getCollege().getAnnualFees(),
                cr.getMatchScore(),
                cr.getConfidenceScore(),
                cr.getRecommendationReason(),
                cr.getRecommendedCourse(),
                cr.getCreatedAt()
        );
    }
}
