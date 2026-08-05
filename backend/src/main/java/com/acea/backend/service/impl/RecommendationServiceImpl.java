package com.acea.backend.service.impl;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.recommendation.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.RecommendationService;
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
public class RecommendationServiceImpl implements RecommendationService {

    private final CareerRecommendationRepository careerRecommendationRepository;
    private final RecommendedSkillRepository recommendedSkillRepository;
    private final RecommendedCertificationRepository recommendedCertificationRepository;
    private final RecommendedProjectRepository recommendedProjectRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentResultRepository resultRepository;
    private final SectionScoreRepository sectionScoreRepository;
    private final TraitScoreRepository traitScoreRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public List<RecommendationResponse> generateRecommendations(UUID studentId) {
        log.info("Generating recommendations for studentId={}", studentId);

        // 1. Fetch Student Profile
        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete your profile first."));

        // 2. Fetch Assessment Results
        List<AssessmentResult> results = resultRepository.findByStudentIdAndStatusOrderByCompletedAtDesc(studentId, ResultStatus.COMPLETED);

        Optional<AssessmentResult> aptitudeRes = results.stream().filter(r -> r.getAssessmentType() == AssessmentType.APTITUDE).findFirst();
        Optional<AssessmentResult> personalityRes = results.stream().filter(r -> r.getAssessmentType() == AssessmentType.PERSONALITY).findFirst();
        Optional<AssessmentResult> interestRes = results.stream().filter(r -> r.getAssessmentType() == AssessmentType.INTEREST).findFirst();

        if (aptitudeRes.isEmpty() || personalityRes.isEmpty() || interestRes.isEmpty()) {
            throw new BadRequestException(
                    ErrorCode.VALIDATION_FAILED,
                    "You must complete all three assessments (Aptitude, Personality, and Interest) before generating recommendations."
            );
        }

        // 3. Extract Section and Trait Scores
        Map<String, BigDecimal> aptitudeScores = sectionScoreRepository
                .findByResultIdOrderByCategoryNameAsc(aptitudeRes.get().getId())
                .stream()
                .collect(Collectors.toMap(SectionScore::getCategoryName, SectionScore::getPercentage, (v1, v2) -> v1));

        Map<String, BigDecimal> personalityTraits = traitScoreRepository
                .findByResultIdOrderByNormalizedScoreDesc(personalityRes.get().getId())
                .stream()
                .collect(Collectors.toMap(TraitScore::getTraitKey, TraitScore::getNormalizedScore, (v1, v2) -> v1));

        Map<String, BigDecimal> riasecProfile = traitScoreRepository
                .findByResultIdOrderByNormalizedScoreDesc(interestRes.get().getId())
                .stream()
                .collect(Collectors.toMap(TraitScore::getTraitKey, TraitScore::getNormalizedScore, (v1, v2) -> v1));

        // 4. Formulate FastAPI Request
        List<String> currentSkillsList = profile.getSkills().stream().map(Skill::getSkillName).toList();
        List<String> interestsList = profile.getInterests().stream().map(Interest::getInterestName).toList();
        List<String> goalsList = profile.getGoals().stream().map(Goal::getGoalTitle).toList();

        AiServiceRequest aiRequest = new AiServiceRequest(
                profile.getEducationLevel(),
                profile.getCurrentCourse(),
                profile.getBranch(),
                profile.getCurrentYear(),
                profile.getCurrentCgpa(),
                currentSkillsList,
                interestsList,
                goalsList,
                profile.getLanguagesKnown(),
                aptitudeScores,
                personalityTraits,
                riasecProfile
        );

        // 5. Invoke AI Service
        String aiUrl = appProperties.aiServiceUrl() + "/api/recommend";
        log.info("Calling FastAPI Service at URL: {}", aiUrl);

        AiServiceResponse aiResponse;
        try {
            ResponseEntity<AiServiceResponse> responseEntity = restTemplate.postForEntity(aiUrl, aiRequest, AiServiceResponse.class);
            aiResponse = responseEntity.getBody();
            if (aiResponse == null || aiResponse.recommendations() == null) {
                throw new IllegalStateException("AI Service returned empty recommendations.");
            }
        } catch (Exception e) {
            log.error("Failed to connect or retrieve recommendations from AI service", e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The recommendation engine is currently unavailable. Please try again later."
            );
        }

        // 6. Delete old recommendations
        deleteRecommendations(studentId);

        // 7. Parse and Save new recommendations
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + studentId));

        List<CareerRecommendation> savedRecs = new ArrayList<>();

        for (AiServiceResponse.CareerRecommendationDto dto : aiResponse.recommendations()) {
            CareerRecommendation rec = CareerRecommendation.builder()
                    .student(student)
                    .careerName(dto.careerName())
                    .careerDescription(dto.careerDescription())
                    .confidenceScore(dto.confidenceScore())
                    .matchScore(dto.matchScore())
                    .reason(dto.reason())
                    .expectedSalary(dto.expectedSalary())
                    .industryGrowth(dto.industryGrowth())
                    .requiredDegree(dto.requiredDegree())
                    .futureScope(dto.futureScope())
                    .jobDemand(dto.jobDemand())
                    .estimatedLearningTime(dto.estimatedLearningTime())
                    .pros(String.join(",", dto.pros()))
                    .challenges(String.join(",", dto.challenges()))
                    .isBookmarked(false)
                    .build();

            // Set up skills
            List<RecommendedSkill> skills = dto.requiredSkills().stream()
                    .map(s -> RecommendedSkill.builder()
                            .student(student)
                            .careerRecommendation(rec)
                            .skillName(s.skillName())
                            .priority(s.priority())
                            .confidence(s.confidence())
                            .build())
                    .toList();
            rec.setSkills(skills);

            // Set up certs
            List<RecommendedCertification> certs = dto.recommendedCertifications().stream()
                    .map(c -> RecommendedCertification.builder()
                            .student(student)
                            .careerRecommendation(rec)
                            .certificationName(c.certificationName())
                            .provider(c.provider())
                            .confidence(c.confidence())
                            .build())
                    .toList();
            rec.setCertifications(certs);

            // Set up projects
            List<RecommendedProject> projects = dto.suggestedProjects().stream()
                    .map(p -> RecommendedProject.builder()
                            .student(student)
                            .careerRecommendation(rec)
                            .projectTitle(p.projectTitle())
                            .projectDescription(p.projectDescription())
                            .difficulty(p.difficulty())
                            .estimatedDuration(p.estimatedDuration())
                            .build())
                    .toList();
            rec.setProjects(projects);

            savedRecs.add(careerRecommendationRepository.save(rec));
        }

        log.info("Saved {} new career recommendations for studentId={}", savedRecs.size(), studentId);

        return savedRecs.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendations(UUID studentId) {
        return careerRecommendationRepository.findByStudentIdOrderByConfidenceScoreDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponse> getCareers(UUID studentId) {
        return getRecommendations(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponse.RecommendedSkillResponse> getSkills(UUID studentId) {
        return recommendedSkillRepository.findByStudentId(studentId)
                .stream()
                .map(s -> new RecommendationResponse.RecommendedSkillResponse(
                        s.getId(), s.getSkillName(), s.getPriority(), s.getConfidence()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponse.RecommendedCertificationResponse> getCertifications(UUID studentId) {
        return recommendedCertificationRepository.findByStudentId(studentId)
                .stream()
                .map(c -> new RecommendationResponse.RecommendedCertificationResponse(
                        c.getId(), c.getCertificationName(), c.getProvider(), c.getConfidence()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationResponse.RecommendedProjectResponse> getProjects(UUID studentId) {
        return recommendedProjectRepository.findByStudentId(studentId)
                .stream()
                .map(p -> new RecommendationResponse.RecommendedProjectResponse(
                        p.getId(), p.getProjectTitle(), p.getProjectDescription(), p.getDifficulty(), p.getEstimatedDuration()
                ))
                .toList();
    }

    @Override
    @Transactional
    public RecommendationResponse toggleBookmark(UUID studentId, UUID recommendationId) {
        CareerRecommendation rec = careerRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found: " + recommendationId));

        if (!rec.getStudent().getId().equals(studentId)) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED_RESULT_ACCESS, "Access denied to recommendation.");
        }

        rec.setIsBookmarked(!rec.getIsBookmarked());
        CareerRecommendation saved = careerRecommendationRepository.save(rec);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRecommendations(UUID studentId) {
        log.info("Deleting existing recommendations for studentId={}", studentId);
        recommendedSkillRepository.deleteByStudentId(studentId);
        recommendedCertificationRepository.deleteByStudentId(studentId);
        recommendedProjectRepository.deleteByStudentId(studentId);
        careerRecommendationRepository.deleteByStudentId(studentId);
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private RecommendationResponse toResponse(CareerRecommendation rec) {
        List<RecommendationResponse.RecommendedSkillResponse> skills = rec.getSkills().stream()
                .map(s -> new RecommendationResponse.RecommendedSkillResponse(
                        s.getId(), s.getSkillName(), s.getPriority(), s.getConfidence()
                ))
                .toList();

        List<RecommendationResponse.RecommendedCertificationResponse> certs = rec.getCertifications().stream()
                .map(c -> new RecommendationResponse.RecommendedCertificationResponse(
                        c.getId(), c.getCertificationName(), c.getProvider(), c.getConfidence()
                ))
                .toList();

        List<RecommendationResponse.RecommendedProjectResponse> projects = rec.getProjects().stream()
                .map(p -> new RecommendationResponse.RecommendedProjectResponse(
                        p.getId(), p.getProjectTitle(), p.getProjectDescription(), p.getDifficulty(), p.getEstimatedDuration()
                ))
                .toList();

        List<String> prosList = rec.getPros() != null && !rec.getPros().isEmpty()
                ? Arrays.asList(rec.getPros().split(","))
                : Collections.emptyList();

        List<String> challengesList = rec.getChallenges() != null && !rec.getChallenges().isEmpty()
                ? Arrays.asList(rec.getChallenges().split(","))
                : Collections.emptyList();

        return new RecommendationResponse(
                rec.getId(),
                rec.getCareerName(),
                rec.getCareerDescription(),
                rec.getConfidenceScore(),
                rec.getMatchScore(),
                rec.getReason(),
                rec.getExpectedSalary(),
                rec.getIndustryGrowth(),
                rec.getRequiredDegree(),
                rec.getFutureScope(),
                rec.getJobDemand(),
                rec.getEstimatedLearningTime(),
                prosList,
                challengesList,
                rec.getIsBookmarked(),
                rec.getCreatedAt(),
                skills,
                certs,
                projects
        );
    }
}
