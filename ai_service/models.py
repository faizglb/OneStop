from pydantic import BaseModel, Field
from typing import List, Dict, Optional

class AiServiceRequestModel(BaseModel):
    education_level: str = Field(..., alias="educationLevel")
    current_course: str = Field(..., alias="currentCourse")
    branch: str = Field(..., alias="branch")
    current_year: int = Field(..., alias="currentYear")
    current_cgpa: float = Field(..., alias="currentCgpa")
    current_skills: List[str] = Field(default=[], alias="currentSkills")
    interests: List[str] = Field(default=[])
    career_goals: List[str] = Field(default=[], alias="careerGoals")
    languages_known: List[str] = Field(default=[], alias="languagesKnown")

    # Assessments
    aptitude_scores: Dict[str, float] = Field(default={}, alias="aptitudeScores")
    personality_traits: Dict[str, float] = Field(default={}, alias="personalityTraits")
    riasec_profile: Dict[str, float] = Field(default={}, alias="riasecProfile")

    class Config:
        populate_by_name = True


class RecommendedSkillModel(BaseModel):
    skill_name: str = Field(..., alias="skillName")
    priority: str  # HIGH | MEDIUM | LOW
    confidence: float

    class Config:
        populate_by_name = True


class RecommendedCertificationModel(BaseModel):
    certification_name: str = Field(..., alias="certificationName")
    provider: str
    confidence: float

    class Config:
        populate_by_name = True


class RecommendedProjectModel(BaseModel):
    project_title: str = Field(..., alias="projectTitle")
    project_description: str = Field(..., alias="projectDescription")
    difficulty: str  # BEGINNER | INTERMEDIATE | ADVANCED
    estimated_duration: str = Field(..., alias="estimatedDuration")

    class Config:
        populate_by_name = True


class CareerRecommendationModel(BaseModel):
    career_name: str = Field(..., alias="careerName")
    career_description: str = Field(..., alias="careerDescription")
    confidence_score: float = Field(..., alias="confidenceScore")
    match_score: float = Field(..., alias="matchScore")
    reason: str
    expected_salary: str = Field(..., alias="expectedSalary")
    industry_growth: str = Field(..., alias="industryGrowth")
    required_degree: str = Field(..., alias="requiredDegree")
    future_scope: str = Field(..., alias="futureScope")
    job_demand: str = Field(..., alias="jobDemand")
    estimated_learning_time: str = Field(..., alias="estimatedLearningTime")
    pros: List[str]
    challenges: List[str]
    required_skills: List[RecommendedSkillModel] = Field(..., alias="requiredSkills")
    recommended_certifications: List[RecommendedCertificationModel] = Field(..., alias="recommendedCertifications")
    suggested_projects: List[RecommendedProjectModel] = Field(..., alias="suggestedProjects")

    class Config:
        populate_by_name = True


class AiServiceResponseModel(BaseModel):
    recommendations: List[CareerRecommendationModel]


# ── College Recommendation Models (Sprint 5) ───────────────────────────

class CollegeCourseDto(BaseModel):
    course_id: str = Field(..., alias="courseId")
    course_name: str = Field(..., alias="courseName")
    stream: str
    degree: str

    class Config:
        populate_by_name = True


class CollegeCutoffDto(BaseModel):
    exam_name: str = Field(..., alias="examName")
    category: str
    closing_rank: int = Field(..., alias="closingRank")
    year: int

    class Config:
        populate_by_name = True


class CollegeProfileDto(BaseModel):
    college_id: str = Field(..., alias="collegeId")
    college_name: str = Field(..., alias="collegeName")
    college_type: str = Field(..., alias="collegeType")
    state: str
    city: str
    nirf_ranking: Optional[int] = Field(None, alias="nirfRanking")
    average_package: float = Field(..., alias="averagePackage")
    annual_fees: float = Field(..., alias="annualFees")
    hostel_available: bool = Field(..., alias="hostelAvailable")
    courses: List[CollegeCourseDto] = Field(default=[])
    cutoffs: List[CollegeCutoffDto] = Field(default=[])

    class Config:
        populate_by_name = True


class CollegeAiServiceRequestModel(BaseModel):
    current_cgpa: float = Field(..., alias="currentCgpa")
    career_goals: List[str] = Field(default=[], alias="careerGoals")
    aptitude_scores: Dict[str, float] = Field(default={}, alias="aptitudeScores")
    personality_traits: Dict[str, float] = Field(default={}, alias="personalityTraits")
    riasec_profile: Dict[str, float] = Field(default={}, alias="riasecProfile")

    # Inputs & Preferences
    entrance_exam: str = Field(..., alias="entranceExam")
    exam_rank: int = Field(..., alias="examRank")
    reservation_category: str = Field(..., alias="reservationCategory")
    max_budget: float = Field(..., alias="maxBudget")
    hostel_required: bool = Field(..., alias="hostelRequired")
    location_preference: Optional[str] = Field(None, alias="locationPreference")
    preferred_course: Optional[str] = Field(None, alias="preferredCourse")

    colleges: List[CollegeProfileDto]

    class Config:
        populate_by_name = True


class CollegeRecDtoModel(BaseModel):
    college_id: str = Field(..., alias="collegeId")
    match_score: float = Field(..., alias="matchScore")
    confidence_score: float = Field(..., alias="confidenceScore")
    recommendation_reason: str = Field(..., alias="recommendationReason")
    recommended_course: str = Field(..., alias="recommendedCourse")

    class Config:
        populate_by_name = True


class CollegeAiServiceResponseModel(BaseModel):
    recommendations: List[CollegeRecDtoModel]


# ── Scholarship Recommendation Models (Sprint 6) ──────────────────────

class EligibilityRuleDto(BaseModel):
    rule_type: str = Field(..., alias="ruleType")
    rule_value: str = Field(..., alias="ruleValue")
    eligibility_rule: str = Field(..., alias="eligibilityRule")

    class Config:
        populate_by_name = True


class ScholarshipProfileDto(BaseModel):
    scholarship_id: str = Field(..., alias="scholarshipId")
    scholarship_name: str = Field(..., alias="scholarshipName")
    provider: str
    scholarship_type: str = Field(..., alias="scholarshipType")
    amount: float
    state: Optional[str] = None
    country: str
    education_level: str = Field(..., alias="educationLevel")
    course: Optional[str] = None
    category: Optional[str] = None
    gender: Optional[str] = None
    minority_eligible: bool = Field(..., alias="minorityEligible")
    disability_eligible: bool = Field(..., alias="disabilityEligible")
    income_limit: Optional[float] = Field(None, alias="incomeLimit")
    minimum_marks: Optional[float] = Field(None, alias="minimumMarks")
    renewable: bool
    description: str = ""
    rules: List[EligibilityRuleDto] = Field(default=[])

    class Config:
        populate_by_name = True


class ScholarshipAiRequestModel(BaseModel):
    education_level: str = Field(..., alias="educationLevel")
    current_course: str = Field(..., alias="currentCourse")
    current_cgpa: float = Field(..., alias="currentCgpa")
    family_income: float = Field(..., alias="familyIncome")
    category: str
    gender: str
    state: str
    is_minority: bool = Field(..., alias="isMinority")
    has_disability: bool = Field(..., alias="hasDisability")
    career_goals: List[str] = Field(default=[], alias="careerGoals")
    aptitude_scores: Dict[str, float] = Field(default={}, alias="aptitudeScores")
    personality_traits: Dict[str, float] = Field(default={}, alias="personalityTraits")
    riasec_profile: Dict[str, float] = Field(default={}, alias="riasecProfile")
    recommended_careers: List[str] = Field(default=[], alias="recommendedCareers")
    recommended_colleges: List[str] = Field(default=[], alias="recommendedColleges")

    scholarships: List[ScholarshipProfileDto]

    class Config:
        populate_by_name = True


class ScholarshipRecDtoModel(BaseModel):
    scholarship_id: str = Field(..., alias="scholarshipId")
    eligibility_score: float = Field(..., alias="eligibilityScore")
    confidence_score: float = Field(..., alias="confidenceScore")
    recommendation_reason: str = Field(..., alias="recommendationReason")
    application_status: str = Field(..., alias="applicationStatus") # ELIGIBLE | PARTIALLY_ELIGIBLE | NOT_ELIGIBLE

    class Config:
        populate_by_name = True


class ScholarshipAiResponseModel(BaseModel):
    recommendations: List[ScholarshipRecDtoModel]


# ── Learning Resources Recommendation Models (Sprint 7) ──────────────

class ResourceProfileDto(BaseModel):
    resource_id: str = Field(..., alias="resourceId")
    title: str
    description: str
    platform: str
    resource_type: str = Field(..., alias="resourceType")
    category: str
    skill: str
    difficulty: str
    duration: str
    rating: float
    is_free: bool = Field(..., alias="isFree")

    class Config:
        populate_by_name = True


class LearningAiRequestModel(BaseModel):
    education_level: str = Field(..., alias="educationLevel")
    current_course: str = Field(..., alias="currentCourse")
    current_cgpa: float = Field(..., alias="currentCgpa")
    category: str
    gender: str
    career_goals: List[str] = Field(default=[], alias="careerGoals")
    aptitude_scores: Dict[str, float] = Field(default={}, alias="aptitudeScores")
    personality_traits: Dict[str, float] = Field(default={}, alias="personalityTraits")
    riasec_profile: Dict[str, float] = Field(default={}, alias="riasecProfile")
    recommended_careers: List[str] = Field(default=[], alias="recommendedCareers")
    recommended_colleges: List[str] = Field(default=[], alias="recommendedColleges")
    recommended_scholarships: List[str] = Field(default=[], alias="recommendedScholarships")

    resources: List[ResourceProfileDto]

    class Config:
        populate_by_name = True


class LearningRecDtoModel(BaseModel):
    resource_id: str = Field(..., alias="resourceId")
    recommended_skill: str = Field(..., alias="recommendedSkill")
    priority: str # HIGH | MEDIUM | LOW
    confidence_score: float = Field(..., alias="confidenceScore")
    reason: str
    estimated_completion_time: str = Field(..., alias="estimatedCompletionTime")

    class Config:
        populate_by_name = True


class LearningAiResponseModel(BaseModel):
    recommendations: List[LearningRecDtoModel]


# ── AI Roadmap Generator Models (Sprint 8) ──────────────────────────

class ResourceItemDtoModel(BaseModel):
    id: str
    title: str
    platform: str
    category: str
    skill: str
    difficulty: str
    url: str


class RoadmapAiRequestModel(BaseModel):
    target_career: str = Field(..., alias="targetCareer")
    target_company: Optional[str] = Field(None, alias="targetCompany")
    duration: str
    weekly_hours: int = Field(10, alias="weeklyHours")
    start_date: str = Field(..., alias="startDate")
    education_level: Optional[str] = Field(None, alias="educationLevel")
    current_course: Optional[str] = Field(None, alias="currentCourse")
    current_cgpa: Optional[float] = Field(None, alias="currentCgpa")
    aptitude_scores: Dict[str, float] = Field(default={}, alias="aptitudeScores")
    personality_traits: Dict[str, float] = Field(default={}, alias="personalityTraits")
    riasec_profile: Dict[str, float] = Field(default={}, alias="riasecProfile")

    available_resources: List[ResourceItemDtoModel] = Field(default=[], alias="availableResources")

    class Config:
        populate_by_name = True


class TaskAiDtoModel(BaseModel):
    title: str
    description: str
    task_type: str = Field(..., alias="taskType")
    estimated_hours: int = Field(1, alias="estimatedHours")
    due_date: str = Field(..., alias="dueDate")
    priority: str = "MEDIUM"
    linked_resource_id: Optional[str] = Field(None, alias="linkedResourceId")

    class Config:
        populate_by_name = True


class PhaseAiDtoModel(BaseModel):
    phase_name: str = Field(..., alias="phaseName")
    sequence: int
    description: str
    tasks: List[TaskAiDtoModel]

    class Config:
        populate_by_name = True


class MilestoneAiDtoModel(BaseModel):
    title: str
    description: str
    completion_criteria: str = Field(..., alias="completionCriteria")

    class Config:
        populate_by_name = True


class RoadmapAiResponseModel(BaseModel):
    title: str
    end_date: str = Field(..., alias="endDate")
    phases: List[PhaseAiDtoModel]
    milestones: List[MilestoneAiDtoModel]

    class Config:
        populate_by_name = True


# ── Resume AI Request and Response Models ──────────────────────────────────────

class ResumeAiRequestModel(BaseModel):
    resume_text: str = Field(..., alias="resumeText")
    file_name: str = Field(..., alias="fileName")
    target_career: str = Field(..., alias="targetCareer")
    current_skills: List[str] = Field(default=[], alias="currentSkills")
    career_goals: List[str] = Field(default=[], alias="careerGoals")
    branch: Optional[str] = None
    education_level: Optional[str] = Field(None, alias="educationLevel")

    class Config:
        populate_by_name = True


class ResumeScoreBreakdownDtoModel(BaseModel):
    keyword_score: float = Field(..., alias="keywordScore")
    section_score: float = Field(..., alias="sectionScore")
    formatting_score: float = Field(..., alias="formattingScore")
    experience_score: float = Field(..., alias="experienceScore")
    education_score: float = Field(..., alias="educationScore")
    grammar_score: float = Field(..., alias="grammarScore")

    class Config:
        populate_by_name = True


class ResumeSectionDtoModel(BaseModel):
    section_name: str = Field(..., alias="sectionName")
    detected: bool
    quality: str
    content_snippet: Optional[str] = Field(None, alias="contentSnippet")

    class Config:
        populate_by_name = True


class ResumeKeywordDtoModel(BaseModel):
    keyword: str
    category: str
    found: bool
    frequency: int
    importance: str

    class Config:
        populate_by_name = True


class ResumeSuggestionDtoModel(BaseModel):
    category: str
    priority: str
    title: str
    description: str
    example: Optional[str] = None

    class Config:
        populate_by_name = True


class ResumeAiResponseModel(BaseModel):
    ats_score: float = Field(..., alias="atsScore")
    overall_score: float = Field(..., alias="overallScore")
    word_count: int = Field(..., alias="wordCount")
    page_count: int = Field(..., alias="pageCount")
    score_breakdown: ResumeScoreBreakdownDtoModel = Field(..., alias="scoreBreakdown")
    sections: List[ResumeSectionDtoModel]
    keywords: List[ResumeKeywordDtoModel]
    suggestions: List[ResumeSuggestionDtoModel]

    class Config:
        populate_by_name = True


# ── Skill Gap Analysis Models (Sprint 10) ─────────────────────────────────

class SkillGapItemModel(BaseModel):
    skill_name: str = Field(..., alias="skillName")
    required_level: str = Field(..., alias="requiredLevel") # BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
    current_level: str = Field(..., alias="currentLevel")   # NONE | BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
    gap_percentage: float = Field(..., alias="gapPercentage")
    priority: str # HIGH | MEDIUM | LOW
    estimated_learning_hours: int = Field(..., alias="estimatedLearningHours")
    status: str # MISSING | WEAK | STRONG | EMERGING
    category: str = "TECHNICAL" # TECHNICAL | SOFT_SKILL | CODING

    class Config:
        populate_by_name = True


class ImprovementRecommendationModel(BaseModel):
    title: str
    description: str
    recommendation_type: str = Field(..., alias="recommendationType") # RESOURCE | PROJECT | COURSE | CERTIFICATION | PRACTICE
    resource_id: Optional[str] = Field(None, alias="resourceId")
    project_id: Optional[str] = Field(None, alias="projectId")
    certificate_id: Optional[str] = Field(None, alias="certificateId")
    priority: str = "MEDIUM"
    estimated_duration: str = Field(..., alias="estimatedDuration")
    url: Optional[str] = None

    class Config:
        populate_by_name = True


class ReadinessBreakdownModel(BaseModel):
    overall_readiness_score: float = Field(..., alias="overallReadinessScore")
    technical_readiness: float = Field(..., alias="technicalReadiness")
    soft_skill_readiness: float = Field(..., alias="softSkillReadiness")
    project_readiness: float = Field(..., alias="projectReadiness")
    interview_readiness: float = Field(..., alias="interviewReadiness")
    resume_readiness: float = Field(..., alias="resumeReadiness")
    coding_readiness: float = Field(..., alias="codingReadiness")

    class Config:
        populate_by_name = True


class RadarChartDataModel(BaseModel):
    subject: str
    student: float
    required: float


class HeatmapItemModel(BaseModel):
    skill_name: str = Field(..., alias="skillName")
    category: str
    current_score: float = Field(..., alias="currentScore")
    required_score: float = Field(..., alias="requiredScore")
    gap: float


class PriorityMatrixItemModel(BaseModel):
    skill_name: str = Field(..., alias="skillName")
    importance: float
    effort: float
    quadrant: str # Quick Win | Strategic Investment | Secondary | Low Priority


class LearningTimelineStepModel(BaseModel):
    step_number: int = Field(..., alias="stepNumber")
    phase: str
    title: str
    skills_covered: List[str] = Field(..., alias="skillsCovered")
    estimated_weeks: int = Field(..., alias="estimatedWeeks")


class VisualAnalyticsModel(BaseModel):
    radar_chart: List[RadarChartDataModel] = Field(..., alias="radarChart")
    skill_heatmap: List[HeatmapItemModel] = Field(..., alias="skillHeatmap")
    priority_matrix: List[PriorityMatrixItemModel] = Field(..., alias="priorityMatrix")
    learning_timeline: List[LearningTimelineStepModel] = Field(..., alias="learningTimeline")

    class Config:
        populate_by_name = True


class SmartInsightModel(BaseModel):
    title: str
    description: str
    insight_type: str = Field(..., alias="insightType") # POSITIVE | WARNING | ACTIONABLE | TIP

    class Config:
        populate_by_name = True


class SkillGapAiRequestModel(BaseModel):
    student_id: str = Field(..., alias="studentId")
    target_career: str = Field(..., alias="targetCareer")
    career_id: Optional[str] = Field(None, alias="careerId")
    current_skills: List[str] = Field(default=[], alias="currentSkills")
    profile_projects: List[str] = Field(default=[], alias="profileProjects")
    profile_certifications: List[str] = Field(default=[], alias="profileCertifications")
    resume_keywords: List[str] = Field(default=[], alias="resumeKeywords")
    resume_ats_score: Optional[float] = Field(None, alias="resumeAtsScore")
    assessment_aptitude_scores: Dict[str, float] = Field(default={}, alias="assessmentAptitudeScores")
    roadmap_completion_pct: Optional[int] = Field(None, alias="roadmapCompletionPct")

    class Config:
        populate_by_name = True


class SkillGapAiResponseModel(BaseModel):
    target_career: str = Field(..., alias="targetCareer")
    overall_readiness_score: float = Field(..., alias="overallReadinessScore")
    overall_gap_percentage: float = Field(..., alias="overallGapPercentage")
    estimated_job_readiness_months: int = Field(..., alias="estimatedJobReadinessMonths")
    readiness_breakdown: ReadinessBreakdownModel = Field(..., alias="readinessBreakdown")
    skill_gaps: List[SkillGapItemModel] = Field(..., alias="skillGaps")
    improvement_recommendations: List[ImprovementRecommendationModel] = Field(..., alias="improvementRecommendations")
    visual_analytics: VisualAnalyticsModel = Field(..., alias="visualAnalytics")
    smart_insights: List[SmartInsightModel] = Field(..., alias="smartInsights")

    class Config:
        populate_by_name = True






