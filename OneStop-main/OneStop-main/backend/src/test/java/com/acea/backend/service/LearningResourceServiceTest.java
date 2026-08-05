package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.LearningBookmarkRepository;
import com.acea.backend.domain.repository.LearningProgressRepository;
import com.acea.backend.domain.repository.LearningResourceRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.learning.LearningProgressRequest;
import com.acea.backend.dto.learning.LearningProgressResponse;
import com.acea.backend.dto.learning.LearningResourceResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.impl.LearningResourceServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LearningResourceService — Unit Tests")
class LearningResourceServiceTest {

    @Mock private LearningResourceRepository resourceRepository;
    @Mock private LearningBookmarkRepository bookmarkRepository;
    @Mock private LearningProgressRepository progressRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private LearningResourceServiceImpl resourceService;

    private UUID studentId;
    private User student;
    private UUID resourceId;
    private LearningResource resource;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        student = User.builder().id(studentId).email("student@acea.com").build();
        resourceId = UUID.randomUUID();
        resource = LearningResource.builder()
                .id(resourceId)
                .title("React Basics")
                .description("Intro to React")
                .platform("Coursera")
                .resourceType("Video Course")
                .category("Frameworks")
                .skill("React")
                .difficulty("BEGINNER")
                .duration("10 hours")
                .isFree(true)
                .rating(BigDecimal.valueOf(4.5))
                .resourceUrl("https://coursera.org/react")
                .certificateAvailable(true)
                .build();
    }

    @Test
    @DisplayName("searchResources — executes dynamic search query correctly")
    void searchResources_executesQuery() {
        PageRequest pageable = PageRequest.of(0, 10);
        given(resourceRepository.searchResources("React", null, null, null, null, false, pageable))
                .willReturn(new PageImpl<>(List.of(resource)));

        Page<LearningResourceResponse> res = resourceService.searchResources(
                "React", null, null, null, null, false, studentId, pageable
        );

        assertThat(res.getContent()).hasSize(1);
        assertThat(res.getContent().get(0).title()).isEqualTo("React Basics");
    }

    @Test
    @DisplayName("bookmarkResource — throws BadRequestException if already bookmarked")
    void bookmarkResource_throwsIfDuplicate() {
        given(bookmarkRepository.existsByStudentIdAndResourceId(studentId, resourceId)).willReturn(true);

        assertThatThrownBy(() -> resourceService.bookmarkResource(studentId, resourceId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already bookmarked");
    }

    @Test
    @DisplayName("updateProgress — throws BadRequestException if percentage out of bounds")
    void updateProgress_throwsIfPercentageInvalid() {
        LearningProgressRequest req = new LearningProgressRequest(resourceId, "IN_PROGRESS", 150);

        assertThatThrownBy(() -> resourceService.updateProgress(studentId, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("between 0 and 100");
    }

    @Test
    @DisplayName("updateProgress — completes course when percentage reaches 100")
    void updateProgress_completesCourseAt100() {
        LearningProgressRequest req = new LearningProgressRequest(resourceId, "IN_PROGRESS", 100);
        given(userRepository.findById(studentId)).willReturn(Optional.of(student));
        given(resourceRepository.findById(resourceId)).willReturn(Optional.of(resource));

        LearningProgress mockProgress = LearningProgress.builder()
                .student(student)
                .resource(resource)
                .status("STARTED")
                .progressPercentage(0)
                .build();

        given(progressRepository.findByStudentIdAndResourceId(studentId, resourceId))
                .willReturn(Optional.of(mockProgress));
        given(progressRepository.save(any(LearningProgress.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        LearningProgressResponse res = resourceService.updateProgress(studentId, req);

        assertThat(res.status()).isEqualTo("COMPLETED");
        assertThat(res.progressPercentage()).isEqualTo(100);
        assertThat(res.completedAt()).isNotNull();
    }
}
