package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.College;
import com.acea.backend.domain.entity.CollegeBookmark;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.repository.CollegeBookmarkRepository;
import com.acea.backend.domain.repository.CollegeRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.college.CollegeResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.CollegeBookmarkService;
import com.acea.backend.service.CollegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollegeBookmarkServiceImpl implements CollegeBookmarkService {

    private final CollegeBookmarkRepository bookmarkRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final CollegeService collegeService;

    @Override
    @Transactional
    public CollegeResponse bookmarkCollege(UUID studentId, UUID collegeId) {
        log.info("Bookmarking collegeId={} for studentId={}", collegeId, studentId);

        // Check duplicate
        if (bookmarkRepository.existsByStudentIdAndCollegeId(studentId, collegeId)) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "This college is already bookmarked.");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found: " + collegeId));

        CollegeBookmark bookmark = CollegeBookmark.builder()
                .student(student)
                .college(college)
                .build();

        bookmarkRepository.save(bookmark);
        return collegeService.getCollegeById(collegeId, studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeResponse> getBookmarkedColleges(UUID studentId) {
        return bookmarkRepository.findByStudentIdWithCollege(studentId)
                .stream()
                .map(cb -> collegeService.getCollegeById(cb.getCollege().getId(), studentId))
                .toList();
    }

    @Override
    @Transactional
    public void removeBookmark(UUID studentId, UUID collegeId) {
        log.info("Removing bookmark for collegeId={} and studentId={}", collegeId, studentId);
        CollegeBookmark bookmark = bookmarkRepository.findByStudentIdAndCollegeId(studentId, collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found."));

        bookmarkRepository.delete(bookmark);
    }
}
