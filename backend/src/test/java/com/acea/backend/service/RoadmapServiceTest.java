package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.roadmap.RoadmapProgressResponse;
import com.acea.backend.dto.roadmap.RoadmapTaskResponse;
import com.acea.backend.dto.roadmap.TaskStatusUpdateRequest;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.impl.RoadmapServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoadmapService — Unit Tests")
class RoadmapServiceTest {

    @Mock private RoadmapRepository roadmapRepository;
    @Mock private RoadmapPhaseRepository phaseRepository;
    @Mock private RoadmapTaskRepository taskRepository;
    @Mock private RoadmapMilestoneRepository milestoneRepository;
    @Mock private StudentProfileRepository studentProfileRepository;
    @Mock private AssessmentResultRepository resultRepository;
    @Mock private SectionScoreRepository sectionScoreRepository;
    @Mock private TraitScoreRepository traitScoreRepository;
    @Mock private LearningResourceRepository learningResourceRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private RoadmapServiceImpl roadmapService;

    private UUID studentId;
    private User student;
    private UUID roadmapId;
    private Roadmap roadmap;
    private UUID taskId;
    private RoadmapTask task;
    private RoadmapPhase phase;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        student = User.builder().id(studentId).email("student@acea.com").build();
        roadmapId = UUID.randomUUID();
        roadmap = Roadmap.builder()
                .id(roadmapId)
                .student(student)
                .title("Software Engineer Roadmap")
                .targetCareer("Software Engineer")
                .duration("60-Day Plan")
                .status("ACTIVE")
                .completionPercentage(0)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(60))
                .weeklyHours(12)
                .build();

        phase = RoadmapPhase.builder()
                .id(UUID.randomUUID())
                .roadmap(roadmap)
                .phaseName("Phase 1: Foundation")
                .sequence(1)
                .build();

        taskId = UUID.randomUUID();
        task = RoadmapTask.builder()
                .id(taskId)
                .phase(phase)
                .title("Java Basics")
                .taskType("FOUNDATION")
                .status("PENDING")
                .estimatedHours(2)
                .dueDate(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("updateTaskStatus — completes task and updates roadmap completion percentage")
    void updateTaskStatus_updatesCompletionPercentage() {
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        given(taskRepository.save(any(RoadmapTask.class))).willAnswer(inv -> inv.getArgument(0));
        given(taskRepository.countByRoadmapId(roadmapId)).willReturn(10L);
        given(taskRepository.countCompletedByRoadmapId(roadmapId)).willReturn(5L);

        TaskStatusUpdateRequest req = new TaskStatusUpdateRequest("COMPLETED", null);
        RoadmapTaskResponse res = roadmapService.updateTaskStatus(taskId, studentId, req);

        assertThat(res.status()).isEqualTo("COMPLETED");
        verify(roadmapRepository).save(roadmap);
        assertThat(roadmap.getCompletionPercentage()).isEqualTo(50);
    }

    @Test
    @DisplayName("getRoadmapProgress — throws Exception if no roadmap found")
    void getRoadmapProgress_throwsIfNotFound() {
        given(roadmapRepository.findByStudentIdAndStatus(studentId, "ACTIVE")).willReturn(Optional.empty());
        given(roadmapRepository.findByStudentIdWithDetails(studentId)).willReturn(List.of());

        assertThatThrownBy(() -> roadmapService.getRoadmapProgress(studentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
