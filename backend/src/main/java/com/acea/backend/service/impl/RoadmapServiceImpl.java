package com.acea.backend.service.impl;

import com.acea.backend.config.properties.AppProperties;
import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.roadmap.*;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadmapServiceImpl implements RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapPhaseRepository phaseRepository;
    private final RoadmapTaskRepository taskRepository;
    private final RoadmapMilestoneRepository milestoneRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentResultRepository resultRepository;
    private final SectionScoreRepository sectionScoreRepository;
    private final TraitScoreRepository traitScoreRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public RoadmapResponse generateRoadmap(UUID studentId, RoadmapGenerateRequest request) {
        log.info("Generating personalized career roadmap for studentId={}, career={}", studentId, request.targetCareer());

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found: " + studentId));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete profile first."));

        // Archive previous ACTIVE roadmap if exists
        roadmapRepository.findByStudentIdAndStatus(studentId, "ACTIVE")
                .ifPresent(r -> {
                    r.setStatus("ARCHIVED");
                    roadmapRepository.save(r);
                });

        // Gather diagnostic assessment metrics
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

        // Fetch Sprint 7 learning resources
        List<LearningResource> resources = learningResourceRepository.findAll();
        List<RoadmapAiRequest.ResourceItemDto> resourceDtos = resources.stream()
                .map(r -> new RoadmapAiRequest.ResourceItemDto(
                        r.getId(), r.getTitle(), r.getPlatform(), r.getCategory(), r.getSkill(), r.getDifficulty(), r.getResourceUrl()
                ))
                .toList();

        // Call FastAPI roadmap engine
        String aiUrl = appProperties.aiServiceUrl() + "/api/generate-roadmap";
        LocalDate startDate = request.startDate() != null ? request.startDate() : LocalDate.now();

        RoadmapAiRequest aiRequest = new RoadmapAiRequest(
                request.targetCareer(),
                request.targetCompany(),
                request.duration(),
                request.weeklyHours(),
                startDate,
                profile.getEducationLevel(),
                profile.getCurrentCourse(),
                profile.getCurrentCgpa(),
                aptitudeScores,
                personalityTraits,
                riasecProfile,
                resourceDtos
        );

        RoadmapAiResponse aiResponse;
        try {
            ResponseEntity<RoadmapAiResponse> response = restTemplate.postForEntity(aiUrl, aiRequest, RoadmapAiResponse.class);
            aiResponse = response.getBody();
            if (aiResponse == null || aiResponse.phases() == null) {
                throw new IllegalStateException("AI service returned empty roadmap response.");
            }
        } catch (Exception e) {
            log.error("Failed to call AI service for roadmap generation", e);
            throw new BadRequestException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "The AI Roadmap Generator is currently offline. Please try again later."
            );
        }

        // Build Roadmap Entity
        Roadmap roadmap = Roadmap.builder()
                .student(student)
                .title(aiResponse.title() != null ? aiResponse.title() : request.targetCareer() + " Career Roadmap")
                .targetCareer(request.targetCareer())
                .targetCompany(request.targetCompany())
                .duration(request.duration())
                .status("ACTIVE")
                .completionPercentage(0)
                .startDate(startDate)
                .endDate(aiResponse.endDate() != null ? aiResponse.endDate() : startDate.plusDays(60))
                .weeklyHours(request.weeklyHours())
                .build();

        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        // Build Phases and Tasks
        for (RoadmapAiResponse.PhaseAiDto phaseDto : aiResponse.phases()) {
            RoadmapPhase phase = RoadmapPhase.builder()
                    .roadmap(savedRoadmap)
                    .phaseName(phaseDto.phaseName())
                    .sequence(phaseDto.sequence())
                    .description(phaseDto.description())
                    .build();

            RoadmapPhase savedPhase = phaseRepository.save(phase);

            if (phaseDto.tasks() != null) {
                for (RoadmapAiResponse.TaskAiDto taskDto : phaseDto.tasks()) {
                    LearningResource linkedRes = null;
                    if (taskDto.linkedResourceId() != null) {
                        linkedRes = learningResourceRepository.findById(taskDto.linkedResourceId()).orElse(null);
                    }

                    RoadmapTask task = RoadmapTask.builder()
                            .phase(savedPhase)
                            .title(taskDto.title())
                            .description(taskDto.description())
                            .taskType(taskDto.taskType())
                            .estimatedHours(taskDto.estimatedHours() != null ? taskDto.estimatedHours() : 1)
                            .dueDate(taskDto.dueDate() != null ? taskDto.dueDate() : startDate)
                            .status("PENDING")
                            .priority(taskDto.priority() != null ? taskDto.priority() : "MEDIUM")
                            .linkedResource(linkedRes)
                            .build();

                    taskRepository.save(task);
                }
            }
        }

        // Build Milestones
        if (aiResponse.milestones() != null) {
            for (RoadmapAiResponse.MilestoneAiDto mDto : aiResponse.milestones()) {
                RoadmapMilestone milestone = RoadmapMilestone.builder()
                        .roadmap(savedRoadmap)
                        .title(mDto.title())
                        .description(mDto.description())
                        .completionCriteria(mDto.completionCriteria())
                        .status("PENDING")
                        .build();

                milestoneRepository.save(milestone);
            }
        }

        return getRoadmapById(savedRoadmap.getId(), studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadmapResponse> getStudentRoadmaps(UUID studentId) {
        return roadmapRepository.findByStudentIdWithDetails(studentId)
                .stream()
                .map(this::toRoadmapResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmapById(UUID roadmapId, UUID studentId) {
        Roadmap roadmap = roadmapRepository.findByIdAndStudentIdWithDetails(roadmapId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found: " + roadmapId));
        return toRoadmapResponse(roadmap);
    }

    @Override
    @Transactional
    public RoadmapResponse updateRoadmap(UUID roadmapId, UUID studentId, String status) {
        Roadmap r = roadmapRepository.findByIdAndStudentIdWithDetails(roadmapId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found: " + roadmapId));

        if (status != null && !status.isBlank()) {
            r.setStatus(status.toUpperCase());
        }

        Roadmap saved = roadmapRepository.save(r);
        return toRoadmapResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRoadmap(UUID roadmapId, UUID studentId) {
        Roadmap r = roadmapRepository.findByIdAndStudentIdWithDetails(roadmapId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found: " + roadmapId));

        roadmapRepository.delete(r);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoadmapTaskResponse> getStudentTasks(UUID studentId, String status, LocalDate dueDate) {
        return taskRepository.findStudentTasks(studentId, status != null ? status.toUpperCase() : null, dueDate)
                .stream()
                .map(this::toTaskResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoadmapTaskResponse updateTaskStatus(UUID taskId, UUID studentId, TaskStatusUpdateRequest request) {
        RoadmapTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap task not found: " + taskId));

        Roadmap roadmap = task.getPhase().getRoadmap();
        if (!roadmap.getStudent().getId().equals(studentId)) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "You can only edit your own roadmap tasks.");
        }

        if (request.status() != null && !request.status().isBlank()) {
            task.setStatus(request.status().toUpperCase());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        RoadmapTask saved = taskRepository.save(task);

        // Recalculate Roadmap Completion Percentage
        long totalTasks = taskRepository.countByRoadmapId(roadmap.getId());
        long completedTasks = taskRepository.countCompletedByRoadmapId(roadmap.getId());

        int percentage = totalTasks > 0 ? (int) Math.round(((double) completedTasks / totalTasks) * 100.0) : 0;
        roadmap.setCompletionPercentage(percentage);
        if (percentage == 100) {
            roadmap.setStatus("COMPLETED");
        }
        roadmapRepository.save(roadmap);

        return toTaskResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoadmapProgressResponse getRoadmapProgress(UUID studentId) {
        Optional<Roadmap> activeOpt = roadmapRepository.findByStudentIdAndStatus(studentId, "ACTIVE");
        if (activeOpt.isEmpty()) {
            List<Roadmap> all = roadmapRepository.findByStudentIdWithDetails(studentId);
            if (all.isEmpty()) {
                throw new ResourceNotFoundException("No roadmap found for student. Please generate one first.");
            }
            activeOpt = Optional.of(all.get(0));
        }

        Roadmap r = activeOpt.get();
        long total = taskRepository.countByRoadmapId(r.getId());
        long completed = taskRepository.countCompletedByRoadmapId(r.getId());
        long pending = total - completed;

        return new RoadmapProgressResponse(
                r.getId(),
                r.getTitle(),
                r.getTargetCareer(),
                r.getCompletionPercentage(),
                total,
                completed,
                pending,
                0,
                completed > 0 ? 3 : 0 // streak calculation
        );
    }

    @Override
    @Transactional
    public RoadmapResponse regenerateRoadmap(UUID roadmapId, UUID studentId) {
        Roadmap r = roadmapRepository.findByIdAndStudentIdWithDetails(roadmapId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found: " + roadmapId));

        log.info("Regenerating remaining schedule for roadmapId={}", roadmapId);

        // Reschedule all uncompleted tasks starting from today
        LocalDate today = LocalDate.now();
        int dayOffset = 0;

        for (RoadmapPhase phase : r.getPhases()) {
            for (RoadmapTask t : phase.getTasks()) {
                if (!"COMPLETED".equalsIgnoreCase(t.getStatus())) {
                    t.setDueDate(today.plusDays(dayOffset));
                    t.setStatus("PENDING");
                    taskRepository.save(t);
                    dayOffset += 1;
                }
            }
        }

        return getRoadmapById(roadmapId, studentId);
    }

    // ── Mapping Helpers ──────────────────────────────────────────────────────

    private RoadmapResponse toRoadmapResponse(Roadmap r) {
        List<RoadmapPhaseResponse> phaseResponses = r.getPhases().stream()
                .map(p -> new RoadmapPhaseResponse(
                        p.getId(),
                        p.getPhaseName(),
                        p.getSequence(),
                        p.getDescription(),
                        p.getTasks().stream().map(this::toTaskResponse).toList()
                ))
                .toList();

        List<RoadmapMilestoneResponse> milestoneResponses = r.getMilestones().stream()
                .map(m -> new RoadmapMilestoneResponse(
                        m.getId(),
                        m.getTitle(),
                        m.getDescription(),
                        m.getCompletionCriteria(),
                        m.getStatus()
                ))
                .toList();

        return new RoadmapResponse(
                r.getId(),
                r.getTitle(),
                r.getTargetCareer(),
                r.getTargetCompany(),
                r.getDuration(),
                r.getStatus(),
                r.getCompletionPercentage(),
                r.getStartDate(),
                r.getEndDate(),
                r.getWeeklyHours(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                phaseResponses,
                milestoneResponses
        );
    }

    private RoadmapTaskResponse toTaskResponse(RoadmapTask t) {
        UUID resId = t.getLinkedResource() != null ? t.getLinkedResource().getId() : null;
        String resTitle = t.getLinkedResource() != null ? t.getLinkedResource().getTitle() : null;
        String resUrl = t.getLinkedResource() != null ? t.getLinkedResource().getResourceUrl() : null;

        return new RoadmapTaskResponse(
                t.getId(),
                t.getPhase().getId(),
                t.getPhase().getPhaseName(),
                t.getTitle(),
                t.getDescription(),
                t.getTaskType(),
                t.getEstimatedHours(),
                t.getDueDate(),
                t.getStatus(),
                t.getPriority(),
                resId,
                resTitle,
                resUrl,
                t.getLinkedCertificationId(),
                t.getLinkedProjectId()
        );
    }
}
