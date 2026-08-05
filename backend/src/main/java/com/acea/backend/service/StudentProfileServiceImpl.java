package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;
import com.acea.backend.exception.*;
import com.acea.backend.mapper.StudentProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final SkillRepository skillRepository;
    private final InterestRepository interestRepository;
    private final GoalRepository goalRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;
    
    private final StudentProfileMapper mapper;

    // ── Helper: Validate and Recalculate Completion ───────────────────────────

    private StudentProfile getProfileAndValidateOwnership(UUID userId) {
        return studentProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete your profile initialization first."));
    }

    private void recalculateAndSaveCompletion(StudentProfile profile) {
        int pct = 0;

        // 1. Personal Details (20%)
        if (profile.getCountry() != null && !profile.getCountry().isBlank()
                && profile.getState() != null && !profile.getState().isBlank()
                && profile.getCity() != null && !profile.getCity().isBlank()
                && profile.getDateOfBirth() != null
                && profile.getPhoneNumber() != null && !profile.getPhoneNumber().isBlank()
                && profile.getAddress() != null && !profile.getAddress().isBlank()) {
            pct += 20;
        }

        // 2. Education Info (20%)
        if (profile.getSchoolName() != null && !profile.getSchoolName().isBlank()
                && profile.getCollegeName() != null && !profile.getCollegeName().isBlank()
                && profile.getCurrentCourse() != null && !profile.getCurrentCourse().isBlank()
                && profile.getBranch() != null && !profile.getBranch().isBlank()
                && profile.getCurrentYear() != null
                && profile.getCurrentCgpa() != null
                && profile.getTenthPercentage() != null
                && profile.getTwelfthPercentage() != null) {
            pct += 20;
        }

        // 3. Skills (15%)
        if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
            pct += 15;
        }

        // 4. Interests (10%)
        if (profile.getInterests() != null && !profile.getInterests().isEmpty()) {
            pct += 10;
        }

        // 5. Goals (10%)
        if (profile.getGoals() != null && !profile.getGoals().isEmpty()) {
            pct += 10;
        }

        // 6. Projects (10%)
        if (profile.getProjects() != null && !profile.getProjects().isEmpty()) {
            pct += 10;
        }

        // 7. Certificates (5%)
        if (profile.getCertifications() != null && !profile.getCertifications().isEmpty()) {
            pct += 5;
        }

        // 8. Resume (10%)
        if (profile.getResumeUrl() != null && !profile.getResumeUrl().isBlank()) {
            pct += 10;
        }

        profile.setProfileCompletionPct(pct);
        studentProfileRepository.save(profile);
    }

    // ── Profile CRUD ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        return mapper.toResponse(profile);
    }

    @Override
    public StudentProfileResponse createOrUpdateProfile(UUID userId, StudentProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User account not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            log.info("Initializing new student profile for user: {}", userId);
            profile = mapper.toEntity(request);
            profile.setUser(user);
        } else {
            log.info("Updating existing student profile for user: {}", userId);
            mapper.updateEntityFromRequest(request, profile);
        }

        studentProfileRepository.save(profile);
        recalculateAndSaveCompletion(profile);
        return mapper.toResponse(profile);
    }

    @Override
    public void deleteProfile(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        log.info("Deleting student profile: {}", profile.getId());
        studentProfileRepository.delete(profile);
    }

    // ── Skills CRUD ───────────────────────────────────────────────────────────

    @Override
    public SkillResponse addSkill(UUID userId, SkillRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Skill skill = mapper.toEntity(request);
        skill.setStudentProfile(profile);
        
        skillRepository.save(skill);
        profile.getSkills().add(skill);
        recalculateAndSaveCompletion(profile);
        
        return mapper.toResponse(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getSkills(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        return mapper.toSkillResponseList(profile.getSkills());
    }

    @Override
    public SkillResponse updateSkill(UUID userId, UUID skillId, SkillRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        
        if (!skill.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to modify this skill");
        }
        
        mapper.updateEntityFromRequest(request, skill);
        skillRepository.save(skill);
        return mapper.toResponse(skill);
    }

    @Override
    public void deleteSkill(UUID userId, UUID skillId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        
        if (!skill.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to delete this skill");
        }
        
        profile.getSkills().remove(skill);
        skillRepository.delete(skill);
        recalculateAndSaveCompletion(profile);
    }

    // ── Interests CRUD ────────────────────────────────────────────────────────

    @Override
    public InterestResponse addInterest(UUID userId, InterestRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Interest interest = mapper.toEntity(request);
        interest.setStudentProfile(profile);
        
        interestRepository.save(interest);
        profile.getInterests().add(interest);
        recalculateAndSaveCompletion(profile);
        
        return mapper.toResponse(interest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterestResponse> getInterests(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        return mapper.toInterestResponseList(profile.getInterests());
    }

    @Override
    public void deleteInterest(UUID userId, UUID interestId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Interest interest = interestRepository.findById(interestId)
            .orElseThrow(() -> new ResourceNotFoundException("Interest not found"));
        
        if (!interest.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to delete this interest");
        }
        
        profile.getInterests().remove(interest);
        interestRepository.delete(interest);
        recalculateAndSaveCompletion(profile);
    }

    // ── Goals CRUD ────────────────────────────────────────────────────────────

    @Override
    public GoalResponse addGoal(UUID userId, GoalRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Goal goal = mapper.toEntity(request);
        goal.setStudentProfile(profile);
        
        goalRepository.save(goal);
        profile.getGoals().add(goal);
        recalculateAndSaveCompletion(profile);
        
        return mapper.toResponse(goal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponse> getGoals(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        return mapper.toGoalResponseList(profile.getGoals());
    }

    @Override
    public GoalResponse updateGoal(UUID userId, UUID goalId, GoalRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        
        if (!goal.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to modify this goal");
        }
        
        mapper.updateEntityFromRequest(request, goal);
        goalRepository.save(goal);
        return mapper.toResponse(goal);
    }

    @Override
    public void deleteGoal(UUID userId, UUID goalId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        
        if (!goal.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to delete this goal");
        }
        
        profile.getGoals().remove(goal);
        goalRepository.delete(goal);
        recalculateAndSaveCompletion(profile);
    }

    // ── Projects CRUD ─────────────────────────────────────────────────────────

    @Override
    public ProjectResponse addProject(UUID userId, ProjectRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Project project = mapper.toEntity(request);
        project.setStudentProfile(profile);
        
        projectRepository.save(project);
        profile.getProjects().add(project);
        recalculateAndSaveCompletion(profile);
        
        return mapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        return mapper.toProjectResponseList(profile.getProjects());
    }

    @Override
    public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to modify this project");
        }
        
        mapper.updateEntityFromRequest(request, project);
        projectRepository.save(project);
        return mapper.toResponse(project);
    }

    @Override
    public void deleteProject(UUID userId, UUID projectId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to delete this project");
        }
        
        profile.getProjects().remove(project);
        projectRepository.delete(project);
        recalculateAndSaveCompletion(profile);
    }

    // ── Certifications CRUD ───────────────────────────────────────────────────

    @Override
    public CertificationResponse addCertification(UUID userId, CertificationRequest request) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Certification certification = mapper.toEntity(request);
        certification.setStudentProfile(profile);
        
        certificationRepository.save(certification);
        profile.getCertifications().add(certification);
        recalculateAndSaveCompletion(profile);
        
        return mapper.toResponse(certification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponse> getCertifications(UUID userId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        return mapper.toCertificationResponseList(profile.getCertifications());
    }

    @Override
    public void deleteCertification(UUID userId, UUID certificationId) {
        StudentProfile profile = getProfileAndValidateOwnership(userId);
        Certification certification = certificationRepository.findById(certificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));
        
        if (!certification.getStudentProfile().getId().equals(profile.getId())) {
            throw new ForbiddenException("You are not authorized to delete this certification");
        }
        
        profile.getCertifications().remove(certification);
        certificationRepository.delete(certification);
        recalculateAndSaveCompletion(profile);
    }
}
