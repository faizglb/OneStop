package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;
import com.acea.backend.exception.ForbiddenException;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.mapper.StudentProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentProfileMapper mapper;

    @InjectMocks
    private StudentProfileServiceImpl profileService;

    private UUID userId;
    private User user;
    private StudentProfile profile;
    private StudentProfileRequest profileRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("student@acea.io")
                .firstName("Rahul")
                .lastName("Sharma")
                .build();

        profile = StudentProfile.builder()
                .id(UUID.randomUUID())
                .user(user)
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2003, 5, 10))
                .gender("MALE")
                .phoneNumber("+919876543210")
                .country("India")
                .state("Maharashtra")
                .district("Mumbai City")
                .city("Mumbai")
                .address("123, Marine Drive")
                .pinCode("400001")
                .schoolName("KV No. 1")
                .collegeName("IIT Bombay")
                .currentCourse("B.Tech")
                .branch("Computer Science")
                .currentYear(3)
                .educationLevel("UNDERGRADUATE")
                .tenthPercentage(BigDecimal.valueOf(92.5))
                .twelfthPercentage(BigDecimal.valueOf(90.0))
                .currentCgpa(BigDecimal.valueOf(9.2))
                .preferredStudyMode("OFFLINE")
                .familyIncome(BigDecimal.valueOf(800000.0))
                .category("GENERAL")
                .caste("Brahmin")
                .disabilityStatus("NONE")
                .languagesKnown(Arrays.asList("English", "Hindi", "Marathi"))
                .careerGoal("Software Architect")
                .bio("Aspiring tech leader interested in cloud systems.")
                .skills(new ArrayList<>())
                .interests(new ArrayList<>())
                .goals(new ArrayList<>())
                .projects(new ArrayList<>())
                .certifications(new ArrayList<>())
                .build();

        profileRequest = StudentProfileRequest.builder()
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2003, 5, 10))
                .gender("MALE")
                .phoneNumber("+919876543210")
                .country("India")
                .state("Maharashtra")
                .district("Mumbai City")
                .city("Mumbai")
                .address("123, Marine Drive")
                .pinCode("400001")
                .schoolName("KV No. 1")
                .collegeName("IIT Bombay")
                .currentCourse("B.Tech")
                .branch("Computer Science")
                .currentYear(3)
                .educationLevel("UNDERGRADUATE")
                .tenthPercentage(BigDecimal.valueOf(92.5))
                .twelfthPercentage(BigDecimal.valueOf(90.0))
                .currentCgpa(BigDecimal.valueOf(9.2))
                .preferredStudyMode("OFFLINE")
                .familyIncome(BigDecimal.valueOf(800000.0))
                .category("GENERAL")
                .caste("Brahmin")
                .disabilityStatus("NONE")
                .languagesKnown(Arrays.asList("English", "Hindi", "Marathi"))
                .careerGoal("Software Architect")
                .bio("Aspiring tech leader interested in cloud systems.")
                .build();
    }

    @Test
    void getProfile_Success() {
        when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(mapper.toResponse(profile)).thenReturn(mock(StudentProfileResponse.class));

        assertNotNull(profileService.getProfile(userId));
        verify(studentProfileRepository).findByUserId(userId);
    }

    @Test
    void getProfile_NotFound() {
        when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> profileService.getProfile(userId));
    }

    @Test
    void createProfile_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(mapper.toEntity(profileRequest)).thenReturn(profile);
        when(studentProfileRepository.save(any(StudentProfile.class))).thenReturn(profile);
        when(mapper.toResponse(profile)).thenReturn(mock(StudentProfileResponse.class));

        assertNotNull(profileService.createOrUpdateProfile(userId, profileRequest));
        
        // Assert recalculation logic runs
        verify(studentProfileRepository, times(2)).save(profile);
        assertEquals(40, profile.getProfileCompletionPct()); // 20% Personal + 20% Academic = 40%
    }

    @Test
    void addSkill_Success() {
        when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        SkillRequest skillReq = new SkillRequest("Java", "ADVANCED", 24);
        Skill skill = Skill.builder().id(UUID.randomUUID()).skillName("Java").skillLevel("ADVANCED").experienceMonths(24).build();
        
        when(mapper.toEntity(skillReq)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(mapper.toResponse(skill)).thenReturn(mock(SkillResponse.class));

        assertNotNull(profileService.addSkill(userId, skillReq));
        
        assertTrue(profile.getSkills().contains(skill));
        assertEquals(55, profile.getProfileCompletionPct()); // 40% base + 15% skill = 55%
        verify(skillRepository).save(skill);
    }

    @Test
    void updateSkill_Forbidden() {
        when(studentProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        
        StudentProfile otherProfile = StudentProfile.builder().id(UUID.randomUUID()).build();
        Skill otherSkill = Skill.builder().id(UUID.randomUUID()).studentProfile(otherProfile).build();
        
        when(skillRepository.findById(otherSkill.getId())).thenReturn(Optional.of(otherSkill));
        
        SkillRequest skillReq = new SkillRequest("Java", "ADVANCED", 24);
        
        assertThrows(ForbiddenException.class, () -> 
            profileService.updateSkill(userId, otherSkill.getId(), skillReq));
    }
}
