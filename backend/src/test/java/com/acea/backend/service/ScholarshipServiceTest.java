package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.ScholarshipApplicationRepository;
import com.acea.backend.domain.repository.ScholarshipBookmarkRepository;
import com.acea.backend.domain.repository.ScholarshipRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.scholarship.ScholarshipApplicationHistoryResponse;
import com.acea.backend.dto.scholarship.ScholarshipApplicationStatusRequest;
import com.acea.backend.dto.scholarship.ScholarshipResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.impl.ScholarshipBookmarkServiceImpl;
import com.acea.backend.service.impl.ScholarshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScholarshipService — Unit Tests")
class ScholarshipServiceTest {

    @Mock private ScholarshipRepository scholarshipRepository;
    @Mock private ScholarshipBookmarkRepository bookmarkRepository;
    @Mock private ScholarshipApplicationRepository applicationRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ScholarshipServiceImpl scholarshipService;
    @InjectMocks private ScholarshipBookmarkServiceImpl bookmarkService;

    private UUID scholarshipId;
    private Scholarship scholarship;
    private UUID studentId;
    private User student;

    @BeforeEach
    void setUp() {
        scholarshipId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        student = User.builder().id(studentId).email("student@acea.com").build();
        scholarship = Scholarship.builder()
                .id(scholarshipId)
                .scholarshipName("Merit Scholarship")
                .provider("MHRD")
                .scholarshipType("Government")
                .description("Merit based funding")
                .amount(BigDecimal.valueOf(20000.00))
                .lastDate(LocalDate.now().plusDays(30))
                .educationLevel("UNDERGRADUATE")
                .requiredDocuments("Marksheet")
                .build();
    }

    @Test
    @DisplayName("getScholarshipById — returns details successfully")
    void getScholarshipById_returnsDetails() {
        given(scholarshipRepository.findByIdWithDetails(scholarshipId)).willReturn(Optional.of(scholarship));
        given(bookmarkRepository.existsByStudentIdAndScholarshipId(studentId, scholarshipId)).willReturn(true);

        ScholarshipResponse res = scholarshipService.getScholarshipById(scholarshipId, studentId);

        assertThat(res.scholarshipName()).isEqualTo("Merit Scholarship");
        assertThat(res.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("getScholarshipById — throws when not found")
    void getScholarshipById_throwsWhenNotFound() {
        given(scholarshipRepository.findByIdWithDetails(scholarshipId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> scholarshipService.getScholarshipById(scholarshipId, studentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateApplicationStatus — throws BadRequestException if reminderDate is in past")
    void updateApplicationStatus_throwsIfReminderInPast() {
        ScholarshipApplicationStatusRequest req = new ScholarshipApplicationStatusRequest(
                scholarshipId, "APPLIED", "Notes", Instant.now().minusSeconds(100)
        );

        assertThatThrownBy(() -> scholarshipService.updateApplicationStatus(studentId, req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Reminder date must be in the future");
    }

    @Test
    @DisplayName("bookmarkScholarship — throws BadRequestException if already bookmarked")
    void bookmarkScholarship_throwsIfDuplicate() {
        given(bookmarkRepository.existsByStudentIdAndScholarshipId(studentId, scholarshipId)).willReturn(true);

        assertThatThrownBy(() -> bookmarkService.bookmarkScholarship(studentId, scholarshipId))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already bookmarked");
    }
}
