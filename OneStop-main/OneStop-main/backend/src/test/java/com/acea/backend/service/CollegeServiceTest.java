package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.CollegeBookmarkRepository;
import com.acea.backend.domain.repository.CollegeRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.college.CollegeCompareResponse;
import com.acea.backend.dto.college.CollegeResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.impl.CollegeBookmarkServiceImpl;
import com.acea.backend.service.impl.CollegeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CollegeService — Unit Tests")
class CollegeServiceTest {

    @Mock private CollegeRepository collegeRepository;
    @Mock private CollegeBookmarkRepository bookmarkRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CollegeServiceImpl collegeService;
    @InjectMocks private CollegeBookmarkServiceImpl bookmarkService;

    private UUID collegeId;
    private College college;
    private UUID studentId;
    private User student;

    @BeforeEach
    void setUp() {
        collegeId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        student = User.builder().id(studentId).email("student@acea.com").build();
        college = College.builder()
                .id(collegeId)
                .collegeName("IIT Delhi")
                .collegeType("Government")
                .ownership("Public")
                .state("Delhi")
                .city("New Delhi")
                .address("Hauz Khas")
                .annualFees(BigDecimal.valueOf(220000.00))
                .placementPercentage(BigDecimal.valueOf(95.00))
                .averagePackage(BigDecimal.valueOf(2000000.00))
                .establishedYear(1961)
                .build();
    }

    @Test
    @DisplayName("getCollegeById — returns college details successfully")
    void getCollegeById_returnsDetails() {
        given(collegeRepository.findByIdWithDetails(collegeId)).willReturn(Optional.of(college));
        given(bookmarkRepository.existsByStudentIdAndCollegeId(studentId, collegeId)).willReturn(true);

        CollegeResponse res = collegeService.getCollegeById(collegeId, studentId);

        assertThat(res.collegeName()).isEqualTo("IIT Delhi");
        assertThat(res.isBookmarked()).isTrue();
    }

    @Test
    @DisplayName("getCollegeById — throws ResourceNotFoundException when not found")
    void getCollegeById_throwsWhenNotFound() {
        given(collegeRepository.findByIdWithDetails(collegeId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> collegeService.getCollegeById(collegeId, studentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("compareColleges — throws BadRequestException if more than 3 colleges")
    void compareColleges_throwsIfExceedsLimit() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        assertThatThrownBy(() -> collegeService.compareColleges(ids))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("maximum of 3 colleges");
    }

    @Test
    @DisplayName("compareColleges — returns valid matrix data")
    void compareColleges_returnsCompareMatrix() {
        given(collegeRepository.findByIdWithDetails(collegeId)).willReturn(Optional.of(college));

        CollegeCompareResponse res = collegeService.compareColleges(List.of(collegeId));

        assertThat(res.colleges()).hasSize(1);
        assertThat(res.colleges().get(0).collegeName()).isEqualTo("IIT Delhi");
    }
}
