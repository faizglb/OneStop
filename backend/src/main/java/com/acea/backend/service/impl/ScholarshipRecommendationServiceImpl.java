package com.acea.backend.service.impl;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.scholarship.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.ScholarshipRecommendationService;
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
public class ScholarshipRecommendationServiceImpl implements ScholarshipRecommendationService {

    private final ScholarshipRecommendationRepository scholarshipRecommendationRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentResultRepository resultRepository;
    private final SectionScoreRepository sectionScoreRepository;
    private final TraitScoreRepository traitScoreRepository;
    private final CareerRecommendationRepository careerRepository;
    private final CollegeRecommendationRepository collegeRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public List<ScholarshipRecommendationResponse> generateRecommendations(UUID studentId) {
        log.info("Generating AI scholarship recommendations for studentId={}", studentId);

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

        // 3. Recommended careers and colleges list
        List<String> recommendedCareers = careerRepository.findByStudentIdOrderByConfidenceScoreDesc(studentId)
                .stream()
                .map(CareerRecommendation::getCareerName)
                .toList();

        List<String> recommendedColleges = collegeRepository.findByStudentIdWithCollegeOrderByConfidenceDesc(studentId)
                .stream()
                .map(cr -> cr.getCollege().getCollegeName())
                .toList();

        // 4. Fetch all active scholarships
        List<Scholarship> allScholarships = scholarshipRepository.findAll();
        List<ScholarshipAiRequest.ScholarshipProfileDto> candidates = allScholarships.stream()
                .map(s -> {
                    List<ScholarshipAiRequest.EligibilityRuleDto> rules = s.getEligibilities().stream()
                            .map(r -> new ScholarshipAiRequest.EligibilityRuleDto(r.getRuleType(), r.getRuleValue(), r.getEligibilityRule()))
                            .toList();

                    return new ScholarshipAiRequest.ScholarshipProfileDto(
                            s.getId(), s.getScholarshipName(), s.getProvider(), s.getScholarshipType(),
                            s.getAmount(), s.getState(), s.getCountry(), s.getEducationLevel(),
                            s.getCourse(), s.getCategory(), s.getGender(), s.getMinorityEligible(),
                            s.getDisabilityEligible(), s.getIncomeLimit(), s.getMinimumMarks(),
                            s.getRenewable(), s.getDescription(), rules
                    );
                })
                .toList();

        // 5. Post to AI Service
        boolean isMinority = profile.getReligion() != null && !profile.getReligion().equalsIgnoreCase("Hindu");
        boolean hasDisability = profile.getDisabilityStatus() != null && !profile.getDisabilityStatus().equalsIgnoreCase("None") && !profile.getDisabilityStatus().equalsIgnoreCase("No");

        String aiUrl = appProperties.aiServiceUrl() + "/api/recommend-scholarships";
        ScholarshipAiRequest aiRequest = new ScholarshipAiRequest(
                profile.getEducationLevel(),
                profile.getCurrentCourse(),
                profile.getCurrentCgpa(),
                profile.getFamilyIncome(),
                profile.getCategory(),
                profile.getGender(),
                profile.getState(),
                isMinority,
                hasDisability,
                List.of(profile.getCareerGoal()),
                aptitudeScores,
                personalityTraits,
                riasecProfile,
                recommendedCareers,
                recommendedColleges,
                candidates
        );

        ScholarshipAiResponse aiResponse;
        try {
            ResponseEntity<ScholarshipAiResponse> response = restTemplate.postForEntity(aiUrl, aiRequest, ScholarshipAiResponse.class);
            aiResponse = response.getBody();
            if (aiResponse == null || aiResponse.recommendations() == null) {
                throw new IllegalStateException("AI service returned empty recommendations.");
            }
        } catch (Exception e) {
            log.error("Failed to connect or generate scholarship recommendations from AI service", e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The scholarship recommendation engine is currently offline. Please try again later."
            );
        }

        // 6. Delete old recommendations
        scholarshipRecommendationRepository.deleteByStudentId(studentId);

        // 7. Save new matches
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + studentId));

        List<ScholarshipRecommendation> savedList = new ArrayList<>();
        for (ScholarshipAiResponse.ScholarshipRecDto dto : aiResponse.recommendations()) {
            Scholarship scholarship = scholarshipRepository.findById(dto.scholarshipId())
                    .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found: " + dto.scholarshipId()));

            ScholarshipRecommendation rec = ScholarshipRecommendation.builder()
                    .student(student)
                    .scholarship(scholarship)
                    .eligibilityScore(dto.eligibilityScore())
                    .confidenceScore(dto.confidenceScore())
                    .recommendationReason(dto.recommendationReason())
                    .applicationStatus(dto.applicationStatus())
                    .build();

            savedList.add(scholarshipRecommendationRepository.save(rec));
        }

        return savedList.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipRecommendationResponse> getRecommendations(UUID studentId) {
        return scholarshipRecommendationRepository.findByStudentIdWithScholarshipOrderByEligibilityScoreDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private ScholarshipRecommendationResponse toResponse(ScholarshipRecommendation sr) {
        return new ScholarshipRecommendationResponse(
                sr.getId(), sr.getScholarship().getId(), sr.getScholarship().getScholarshipName(),
                sr.getScholarship().getProvider(), sr.getScholarship().getScholarshipType(),
                sr.getScholarship().getAmount(), sr.getScholarship().getLastDate(),
                sr.getScholarship().getRequiredDocuments(), sr.getScholarship().getOfficialWebsite(),
                sr.getScholarship().getRenewable(), sr.getEligibilityScore(), sr.getConfidenceScore(),
                sr.getRecommendationReason(), sr.getApplicationStatus(), sr.getCreatedAt()
        );
    }
}
