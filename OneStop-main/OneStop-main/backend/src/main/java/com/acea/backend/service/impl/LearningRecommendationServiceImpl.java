package com.acea.backend.service.impl;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.learning.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.LearningRecommendationService;
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
public class LearningRecommendationServiceImpl implements LearningRecommendationService {

    private final LearningRecommendationRepository learningRecommendationRepository;
    private final LearningResourceRepository resourceRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentResultRepository resultRepository;
    private final SectionScoreRepository sectionScoreRepository;
    private final TraitScoreRepository traitScoreRepository;
    private final CareerRecommendationRepository careerRepository;
    private final CollegeRecommendationRepository collegeRepository;
    private final ScholarshipRecommendationRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public List<LearningRecommendationResponse> generateRecommendations(UUID studentId) {
        log.info("Generating AI learning resources recommendations for studentId={}", studentId);

        // 1. Fetch Student Profile
        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete profile first."));

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

        // 3. Recommended careers, colleges, and scholarships list
        List<String> recommendedCareers = careerRepository.findByStudentIdOrderByConfidenceScoreDesc(studentId)
                .stream()
                .map(CareerRecommendation::getCareerName)
                .toList();

        List<String> recommendedColleges = collegeRepository.findByStudentIdWithCollegeOrderByConfidenceDesc(studentId)
                .stream()
                .map(cr -> cr.getCollege().getCollegeName())
                .toList();

        List<String> recommendedScholarships = scholarshipRepository.findByStudentIdWithScholarshipOrderByEligibilityScoreDesc(studentId)
                .stream()
                .map(sr -> sr.getScholarship().getScholarshipName())
                .toList();

        // 4. Fetch all resources
        List<LearningResource> allResources = resourceRepository.findAll();
        List<LearningAiRequest.ResourceProfileDto> candidates = allResources.stream()
                .map(r -> new LearningAiRequest.ResourceProfileDto(
                        r.getId(), r.getTitle(), r.getDescription(), r.getPlatform(), r.getResourceType(),
                        r.getCategory(), r.getSkill(), r.getDifficulty(), r.getDuration(), r.getRating(), r.getIsFree()
                ))
                .toList();

        // 5. Post to AI Service
        String aiUrl = appProperties.aiServiceUrl() + "/api/recommend-resources";
        LearningAiRequest aiRequest = new LearningAiRequest(
                profile.getEducationLevel(),
                profile.getCurrentCourse(),
                profile.getCurrentCgpa(),
                profile.getCategory(),
                profile.getGender(),
                List.of(profile.getCareerGoal()),
                aptitudeScores,
                personalityTraits,
                riasecProfile,
                recommendedCareers,
                recommendedColleges,
                recommendedScholarships,
                candidates
        );

        LearningAiResponse aiResponse;
        try {
            ResponseEntity<LearningAiResponse> response = restTemplate.postForEntity(aiUrl, aiRequest, LearningAiResponse.class);
            aiResponse = response.getBody();
            if (aiResponse == null || aiResponse.recommendations() == null) {
                throw new IllegalStateException("AI service returned empty learning recommendations.");
            }
        } catch (Exception e) {
            log.error("Failed to connect or generate learning recommendations from AI service", e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The learning resources recommendation engine is currently offline. Please try again later."
            );
        }

        // 6. Delete old recommendations
        learningRecommendationRepository.deleteByStudentId(studentId);

        // 7. Save new matches
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + studentId));

        List<LearningRecommendation> savedList = new ArrayList<>();
        for (LearningAiResponse.ResourceRecDto dto : aiResponse.recommendations()) {
            LearningResource resource = resourceRepository.findById(dto.resourceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Learning resource not found: " + dto.resourceId()));

            LearningRecommendation rec = LearningRecommendation.builder()
                    .student(student)
                    .resource(resource)
                    .recommendedSkill(dto.recommendedSkill())
                    .priority(dto.priority())
                    .confidenceScore(dto.confidenceScore())
                    .reason(dto.reason())
                    .estimatedCompletionTime(dto.estimatedCompletionTime())
                    .build();

            savedList.add(learningRecommendationRepository.save(rec));
        }

        return savedList.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningRecommendationResponse> getRecommendations(UUID studentId) {
        return learningRecommendationRepository.findByStudentIdWithResourceOrderByConfidenceScoreDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private LearningRecommendationResponse toResponse(LearningRecommendation lr) {
        return new LearningRecommendationResponse(
                lr.getId(), lr.getResource().getId(), lr.getResource().getTitle(),
                lr.getResource().getPlatform(), lr.getResource().getResourceType(),
                lr.getResource().getDifficulty(), lr.getResource().getDuration(),
                lr.getResource().getIsFree(), lr.getResource().getRating(),
                lr.getResource().getThumbnail(), lr.getResource().getResourceUrl(),
                lr.getRecommendedSkill(), lr.getPriority(), lr.getConfidenceScore(),
                lr.getReason(), lr.getEstimatedCompletionTime(), lr.getCreatedAt()
        );
    }
}
