package com.acea.backend.service;

import com.acea.backend.domain.entity.StudentProfile;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.repository.StudentProfileRepository;
import com.acea.backend.dto.chat.StudentContextDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Service that aggregates the student's profile, career goal, assessment summaries,
 * and recommendations to form a context snapshot passed to the AI chatbot.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatContextAggregatorService {

    private final StudentProfileRepository studentProfileRepository;

    public StudentContextDto buildContextForStudent(User student) {
        StudentContextDto.StudentContextDtoBuilder builder = StudentContextDto.builder()
                .studentName(student.getFirstName() + " " + student.getLastName());

        studentProfileRepository.findByUserId(student.getId()).ifPresent(profile -> {
            builder.careerGoal(profile.getCareerGoal())
                    .branch(profile.getBranch())
                    .educationLevel(profile.getEducationLevel())
                    .currentCgpa(profile.getCurrentCgpa() != null ? profile.getCurrentCgpa().doubleValue() : null)
                    .category(profile.getCategory())
                    .familyIncome(profile.getFamilyIncome() != null ? profile.getFamilyIncome().toPlainString() : null)
                    .skills(profile.getSkills() != null
                            ? profile.getSkills().stream()
                                    .map(s -> s.getSkillName())
                                    .collect(Collectors.toList())
                            : new ArrayList<>())
                    .interests(profile.getInterests() != null
                            ? profile.getInterests().stream()
                                    .map(i -> i.getInterestName())
                                    .collect(Collectors.toList())
                            : new ArrayList<>());
        });

        return builder.build();
    }
}
