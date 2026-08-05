package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.ScholarshipApplicationRepository;
import com.acea.backend.domain.repository.ScholarshipBookmarkRepository;
import com.acea.backend.domain.repository.ScholarshipRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.scholarship.ScholarshipApplicationHistoryResponse;
import com.acea.backend.dto.scholarship.ScholarshipApplicationStatusRequest;
import com.acea.backend.dto.scholarship.ScholarshipResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScholarshipServiceImpl implements ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final ScholarshipBookmarkRepository bookmarkRepository;
    private final ScholarshipApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ScholarshipResponse> searchScholarships(
            String search,
            String state,
            String type,
            String educationLevel,
            String category,
            BigDecimal minAmount,
            BigDecimal maxIncome,
            UUID studentId,
            Pageable pageable) {

        log.debug("Searching scholarships with params: search={}, state={}, type={}", search, state, type);
        Page<Scholarship> page = scholarshipRepository.searchScholarships(
                search, state, type, educationLevel, category, minAmount, maxIncome, pageable
        );

        return page.map(s -> toResponse(s, studentId));
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipResponse getScholarshipById(UUID id, UUID studentId) {
        Scholarship s = scholarshipRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found: " + id));

        return toResponse(s, studentId);
    }

    @Override
    @Transactional
    public ScholarshipApplicationHistoryResponse updateApplicationStatus(UUID studentId, ScholarshipApplicationStatusRequest request) {
        log.info("Updating application tracker status for studentId={}, scholarshipId={}, status={}",
                studentId, request.scholarshipId(), request.status());

        // Validate reminderDate is in future
        if (request.reminderDate() != null && request.reminderDate().isBefore(Instant.now())) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "Reminder date must be in the future.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found."));

        Scholarship scholarship = scholarshipRepository.findById(request.scholarshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found."));

        // If duplicate application entry exists, edit it. Otherwise create it.
        ScholarshipApplication app = applicationRepository
                .findByStudentIdAndScholarshipId(studentId, request.scholarshipId())
                .orElseGet(() -> ScholarshipApplication.builder().student(student).scholarship(scholarship).build());

        app.setStatus(request.status());
        app.setNotes(request.notes());
        app.setReminderDate(request.reminderDate());

        ScholarshipApplication saved = applicationRepository.save(app);
        return toHistoryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipApplicationHistoryResponse> getApplicationHistory(UUID studentId) {
        return applicationRepository.findByStudentIdWithScholarship(studentId)
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ScholarshipResponse toResponse(Scholarship s, UUID studentId) {
        boolean isBookmarked = false;
        String appStatus = null;
        if (studentId != null) {
            isBookmarked = bookmarkRepository.existsByStudentIdAndScholarshipId(studentId, s.getId());
            appStatus = applicationRepository.findByStudentIdAndScholarshipId(studentId, s.getId())
                    .map(ScholarshipApplication::getStatus)
                    .orElse(null);
        }

        List<ScholarshipResponse.EligibilityDto> rules = s.getEligibilities().stream()
                .map(e -> new ScholarshipResponse.EligibilityDto(e.getId(), e.getEligibilityRule(), e.getRuleType(), e.getRuleValue()))
                .toList();

        return new ScholarshipResponse(
                s.getId(), s.getScholarshipName(), s.getProvider(), s.getScholarshipType(),
                s.getDescription(), s.getOfficialWebsite(), s.getApplicationLink(), s.getApplicationMode(),
                s.getAmount(), s.getCurrency(), s.getLastDate(), s.getState(), s.getCountry(),
                s.getEducationLevel(), s.getCourse(), s.getCategory(), s.getGender(),
                s.getMinorityEligible(), s.getDisabilityEligible(), s.getIncomeLimit(), s.getMinimumMarks(),
                s.getEntranceExam(), s.getRenewable(), s.getRequiredDocuments(), s.getStatus(),
                isBookmarked, appStatus, rules
        );
    }

    private ScholarshipApplicationHistoryResponse toHistoryResponse(ScholarshipApplication sa) {
        return new ScholarshipApplicationHistoryResponse(
                sa.getId(), sa.getScholarship().getId(), sa.getScholarship().getScholarshipName(),
                sa.getScholarship().getProvider(), sa.getScholarship().getAmount(), sa.getScholarship().getLastDate(),
                sa.getStatus(), sa.getNotes(), sa.getReminderDate(), sa.getUpdatedAt()
        );
    }
}
