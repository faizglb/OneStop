package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.ScholarshipBookmarkRepository;
import com.acea.backend.domain.repository.ScholarshipRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.scholarship.ScholarshipResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.ScholarshipBookmarkService;
import com.acea.backend.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScholarshipBookmarkServiceImpl implements ScholarshipBookmarkService {

    private final ScholarshipBookmarkRepository bookmarkRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final ScholarshipService scholarshipService;

    @Override
    @Transactional
    public ScholarshipResponse bookmarkScholarship(UUID studentId, UUID scholarshipId) {
        log.info("Bookmarking scholarshipId={} for studentId={}", scholarshipId, studentId);

        // Check duplicate
        if (bookmarkRepository.existsByStudentIdAndScholarshipId(studentId, scholarshipId)) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "This scholarship is already bookmarked.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student user not found."));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found."));

        ScholarshipBookmark bookmark = ScholarshipBookmark.builder()
                .student(student)
                .scholarship(scholarship)
                .build();

        bookmarkRepository.save(bookmark);
        return scholarshipService.getScholarshipById(scholarshipId, studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipResponse> getBookmarkedScholarships(UUID studentId) {
        return bookmarkRepository.findByStudentIdWithScholarship(studentId)
                .stream()
                .map(sb -> scholarshipService.getScholarshipById(sb.getScholarship().getId(), studentId))
                .toList();
    }

    @Override
    @Transactional
    public void removeBookmark(UUID studentId, UUID scholarshipId) {
        log.info("Removing scholarship bookmark for scholarshipId={} and studentId={}", scholarshipId, studentId);
        ScholarshipBookmark bookmark = bookmarkRepository.findByStudentIdAndScholarshipId(studentId, scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found."));

        bookmarkRepository.delete(bookmark);
    }
}
