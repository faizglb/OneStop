package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.College;
import com.acea.backend.domain.entity.Course;
import com.acea.backend.domain.entity.Facility;
import com.acea.backend.domain.repository.CollegeBookmarkRepository;
import com.acea.backend.domain.repository.CollegeRepository;
import com.acea.backend.dto.college.CollegeCompareResponse;
import com.acea.backend.dto.college.CollegeResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.CollegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;
    private final CollegeBookmarkRepository bookmarkRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CollegeResponse> searchColleges(
            String search,
            String state,
            String type,
            BigDecimal maxFees,
            BigDecimal minPlacementPct,
            boolean hostelRequired,
            UUID studentId,
            Pageable pageable) {

        log.debug("Searching colleges with search={}, state={}, type={}, maxFees={}, minPlacementPct={}, hostelRequired={}",
                search, state, type, maxFees, minPlacementPct, hostelRequired);

        Page<College> colleges = collegeRepository.searchColleges(
                search, state, type, maxFees, minPlacementPct, hostelRequired, pageable
        );

        return colleges.map(c -> toResponse(c, studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeResponse getCollegeById(UUID id, UUID studentId) {
        College college = collegeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found: " + id));

        return toResponse(college, studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CollegeCompareResponse compareColleges(List<UUID> collegeIds) {
        if (collegeIds == null || collegeIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "Comparison list cannot be empty.");
        }
        if (collegeIds.size() > 3) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "You can compare a maximum of 3 colleges.");
        }

        List<CollegeCompareResponse.CompareItem> items = collegeIds.stream()
                .map(id -> {
                    College c = collegeRepository.findByIdWithDetails(id)
                            .orElseThrow(() -> new ResourceNotFoundException("College not found: " + id));

                    List<String> facilitiesList = c.getFacilities().stream()
                            .map(Facility::getFacilityName)
                            .toList();

                    List<String> coursesList = c.getCourses().stream()
                            .map(Course::getCourseName)
                            .toList();

                    return new CollegeCompareResponse.CompareItem(
                            c.getId(),
                            c.getCollegeName(),
                            c.getCollegeType(),
                            c.getOwnership(),
                            c.getState(),
                            c.getCity(),
                            c.getNirfRanking(),
                            c.getNaacGrade(),
                            c.getAveragePackage(),
                            c.getHighestPackage(),
                            c.getPlacementPercentage(),
                            c.getAnnualFees(),
                            c.getTotalFees(),
                            c.getCampusArea(),
                            c.getHostelAvailable(),
                            c.getGirlsHostel(),
                            c.getBoysHostel(),
                            facilitiesList,
                            coursesList
                    );
                })
                .toList();

        return new CollegeCompareResponse(items);
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private CollegeResponse toResponse(College c, UUID studentId) {
        boolean isBookmarked = false;
        if (studentId != null) {
            isBookmarked = bookmarkRepository.existsByStudentIdAndCollegeId(studentId, c.getId());
        }

        List<CollegeResponse.CourseDto> courses = c.getCourses().stream()
                .map(co -> new CollegeResponse.CourseDto(
                        co.getId(), co.getCourseName(), co.getStream(),
                        co.getDuration(), co.getDegree(), co.getEligibility(),
                        co.getEntranceExam(), co.getSeatIntake()
                ))
                .toList();

        List<CollegeResponse.FacilityDto> facilities = c.getFacilities().stream()
                .map(f -> new CollegeResponse.FacilityDto(
                        f.getId(), f.getFacilityName(), f.getDescription()
                ))
                .toList();

        return new CollegeResponse(
                c.getId(),
                c.getCollegeName(),
                c.getCollegeType(),
                c.getOwnership(),
                c.getState(),
                c.getDistrict(),
                c.getCity(),
                c.getAddress(),
                c.getLatitude(),
                c.getLongitude(),
                c.getNirfRanking(),
                c.getNaacGrade(),
                c.getAicteApproved(),
                c.getUgcApproved(),
                c.getEstablishedYear(),
                c.getWebsite(),
                c.getEmail(),
                c.getPhone(),
                c.getAveragePackage(),
                c.getHighestPackage(),
                c.getMedianPackage(),
                c.getPlacementPercentage(),
                c.getHostelAvailable(),
                c.getGirlsHostel(),
                c.getBoysHostel(),
                c.getTotalFees(),
                c.getAnnualFees(),
                c.getCampusArea(),
                c.getDescription(),
                isBookmarked,
                courses,
                facilities
        );
    }
}
