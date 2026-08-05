from typing import List, Dict, Tuple, Optional
from models import (
    SkillGapAiRequestModel,
    SkillGapAiResponseModel,
    SkillGapItemModel,
    ImprovementRecommendationModel,
    ReadinessBreakdownModel,
    VisualAnalyticsModel,
    RadarChartDataModel,
    HeatmapItemModel,
    PriorityMatrixItemModel,
    LearningTimelineStepModel,
    SmartInsightModel
)
from recommender.dataset import CAREERS_DATASET

LEVEL_NUMERIC = {
    "NONE": 0.0,
    "BEGINNER": 25.0,
    "INTERMEDIATE": 60.0,
    "ADVANCED": 85.0,
    "EXPERT": 100.0
}

NUMERIC_TO_LEVEL = lambda val: (
    "EXPERT" if val >= 90 else
    "ADVANCED" if val >= 75 else
    "INTERMEDIATE" if val >= 50 else
    "BEGINNER" if val >= 25 else
    "NONE"
)

class SkillGapPipeline:

    @staticmethod
    def analyze_skill_gap(request: SkillGapAiRequestModel) -> SkillGapAiResponseModel:
        target_career = request.target_career
        
        # 1. Career Requirement Engine: Fetch target career profile
        career_profile = None
        for c in CAREERS_DATASET:
            if c["career_name"].lower() == target_career.lower():
                career_profile = c
                break
        
        if not career_profile:
            # Fallback for dynamic target career
            career_profile = {
                "career_name": target_career,
                "skills": ["Java", "Python", "Data Structures", "SQL", "Git", "System Design", "Communication"],
                "certifications": [{"name": "AWS Certified Solutions Architect", "provider": "AWS", "confidence": 85.0}],
                "projects": [{"title": "Scalable Web Application", "description": "Full-stack cloud-native app", "difficulty": "ADVANCED", "duration": "4 weeks"}]
            }

        req_skill_list = career_profile.get("skills", [])
        
        # 2. Skill Extractor: Combine skills from Profile, Resume, Certifications, Projects
        extracted_skills: Dict[str, float] = {}
        for s in request.current_skills:
            extracted_skills[s.strip().lower()] = max(extracted_skills.get(s.strip().lower(), 0.0), 65.0)

        for kw in request.resume_keywords:
            extracted_skills[kw.strip().lower()] = max(extracted_skills.get(kw.strip().lower(), 0.0), 75.0)

        for cert in request.profile_certifications:
            c_low = cert.strip().lower()
            for r_skill in req_skill_list:
                if r_skill.lower() in c_low or c_low in r_skill.lower():
                    extracted_skills[r_skill.lower()] = max(extracted_skills.get(r_skill.lower(), 0.0), 85.0)

        for proj in request.profile_projects:
            p_low = proj.strip().lower()
            for r_skill in req_skill_list:
                if r_skill.lower() in p_low:
                    extracted_skills[r_skill.lower()] = max(extracted_skills.get(r_skill.lower(), 0.0), 70.0)

        # 3 & 4. Skill Comparator & Gap Analyzer
        skill_gaps: List[SkillGapItemModel] = []
        heatmap_items: List[HeatmapItemModel] = []
        priority_matrix_items: List[PriorityMatrixItemModel] = []
        
        soft_skills_set = {"communication", "problem solving", "teamwork", "leadership", "time management", "analytical thinking"}

        for idx, req_skill in enumerate(req_skill_list):
            s_key = req_skill.strip().lower()
            current_val = extracted_skills.get(s_key, 0.0)
            
            # Target required level defaults to ADVANCED (85%) for core skills, INTERMEDIATE (60%) for secondary
            required_val = 85.0 if idx < 4 else 60.0
            gap_pct = max(0.0, required_val - current_val)

            current_lvl_str = NUMERIC_TO_LEVEL(current_val)
            required_lvl_str = NUMERIC_TO_LEVEL(required_val)

            # Determine status
            if current_val == 0.0:
                status = "MISSING"
            elif gap_pct > 20.0:
                status = "WEAK"
            elif current_val >= required_val:
                status = "STRONG"
            else:
                status = "EMERGING"

            # Determine priority
            if idx < 3 and gap_pct > 15.0:
                priority = "HIGH"
            elif gap_pct > 30.0:
                priority = "HIGH"
            elif gap_pct > 10.0:
                priority = "MEDIUM"
            else:
                priority = "LOW"

            category = "SOFT_SKILL" if s_key in soft_skills_set else ("CODING" if s_key in ["java", "python", "javascript", "c++", "sql", "data structures", "algorithms"] else "TECHNICAL")

            est_hours = max(5, int(gap_pct * 0.8))

            skill_gaps.append(SkillGapItemModel(
                skillName=req_skill,
                requiredLevel=required_lvl_str,
                currentLevel=current_lvl_str,
                gapPercentage=round(gap_pct, 2),
                priority=priority,
                estimatedLearningHours=est_hours,
                status=status,
                category=category
            ))

            # Heatmap Item
            heatmap_items.append(HeatmapItemModel(
                skillName=req_skill,
                category=category,
                currentScore=round(current_val, 1),
                requiredScore=round(required_val, 1),
                gap=round(gap_pct, 1)
            ))

            # Priority Matrix Item
            importance = round(0.95 - (idx * 0.08), 2)
            effort = round(max(0.2, gap_pct / 100.0), 2)
            
            if importance >= 0.7 and effort <= 0.5:
                quadrant = "Quick Win"
            elif importance >= 0.7 and effort > 0.5:
                quadrant = "Strategic Investment"
            elif importance < 0.7 and effort <= 0.5:
                quadrant = "Secondary"
            else:
                quadrant = "Low Priority"

            priority_matrix_items.append(PriorityMatrixItemModel(
                skillName=req_skill,
                importance=importance,
                effort=effort,
                quadrant=quadrant
            ))

        # 5. Priority Engine & Readiness Calculations
        tech_gaps = [g for g in skill_gaps if g.category in ["TECHNICAL", "CODING"]]
        soft_gaps = [g for g in skill_gaps if g.category == "SOFT_SKILL"]

        tech_readiness = max(10.0, 100.0 - (sum(g.gap_percentage for g in tech_gaps) / max(1, len(tech_gaps)))) if tech_gaps else 80.0
        soft_readiness = max(20.0, 100.0 - (sum(g.gap_percentage for g in soft_gaps) / max(1, len(soft_gaps)))) if soft_gaps else 85.0
        
        proj_count = len(request.profile_projects)
        project_readiness = min(100.0, max(15.0, proj_count * 30.0 + 10.0))

        apt_scores = request.assessment_aptitude_scores.values()
        interview_readiness = round(sum(apt_scores) / len(apt_scores), 2) if apt_scores else 70.0

        resume_readiness = request.resume_ats_score if request.resume_ats_score is not None else 65.0
        
        coding_gaps = [g for g in skill_gaps if g.category == "CODING"]
        coding_readiness = max(15.0, 100.0 - (sum(g.gap_percentage for g in coding_gaps) / max(1, len(coding_gaps)))) if coding_gaps else 75.0

        overall_readiness = round(
            (tech_readiness * 0.35) +
            (coding_readiness * 0.20) +
            (project_readiness * 0.15) +
            (resume_readiness * 0.10) +
            (interview_readiness * 0.10) +
            (soft_readiness * 0.10),
            2
        )

        overall_gap_pct = round(100.0 - overall_readiness, 2)
        est_months = max(1, int(overall_gap_pct / 12.0))

        readiness_breakdown = ReadinessBreakdownModel(
            overallReadinessScore=overall_readiness,
            technicalReadiness=round(tech_readiness, 2),
            softSkillReadiness=round(soft_readiness, 2),
            projectReadiness=round(project_readiness, 2),
            interviewReadiness=round(interview_readiness, 2),
            resumeReadiness=round(resume_readiness, 2),
            codingReadiness=round(coding_readiness, 2)
        )

        # 6. Improvement Engine: Recommend Courses, Projects, Certifications
        recommendations: List[ImprovementRecommendationModel] = []
        
        # High priority missing skills
        high_gaps = [g for g in skill_gaps if g.priority == "HIGH" or g.status == "MISSING"]
        for g in high_gaps[:3]:
            recommendations.append(ImprovementRecommendationModel(
                title=f"Master {g.skill_name} Fundamentals & Practice",
                description=f"Targeted learning path to bring your {g.skill_name} from {g.current_level} to {g.required_level}.",
                recommendationType="COURSE",
                priority="HIGH",
                estimatedDuration=f"{g.estimated_learning_hours} Hours",
                url=f"https://www.coursera.org/search?query={g.skill_name}"
            ))

        # Project recommendation from dataset
        dataset_projects = career_profile.get("projects", [])
        for p in dataset_projects:
            recommendations.append(ImprovementRecommendationModel(
                title=p.get("title", "Capstone Hands-on Project"),
                description=p.get("description", "Build a production-ready application to bridge project readiness."),
                recommendationType="PROJECT",
                priority="HIGH",
                estimatedDuration=p.get("duration", "4 weeks"),
                url=None
            ))

        # Certification recommendation from dataset
        dataset_certs = career_profile.get("certifications", [])
        for c in dataset_certs:
            recommendations.append(ImprovementRecommendationModel(
                title=c.get("name", "Industry Standard Certification"),
                description=f"Validate your skills with official certification by {c.get('provider', 'Industry Leader')}.",
                recommendationType="CERTIFICATION",
                priority="MEDIUM",
                estimatedDuration="2 months",
                url=None
            ))

        # Practice Questions
        recommendations.append(ImprovementRecommendationModel(
            title=f"Solve Top 50 {target_career} Technical Interview Questions",
            description="Practice algorithmic problem-solving and domain-specific technical concepts.",
            recommendationType="PRACTICE",
            priority="HIGH",
            estimatedDuration="15 Hours",
            url="https://leetcode.com/problemset/all/"
        ))

        # Visual Analytics: Radar Chart Data
        radar_chart = [
            RadarChartDataModel(subject="Technical", student=round(tech_readiness, 1), required=90.0),
            RadarChartDataModel(subject="Coding", student=round(coding_readiness, 1), required=85.0),
            RadarChartDataModel(subject="Projects", student=round(project_readiness, 1), required=80.0),
            RadarChartDataModel(subject="Resume/ATS", student=round(resume_readiness, 1), required=85.0),
            RadarChartDataModel(subject="Interview", student=round(interview_readiness, 1), required=80.0),
            RadarChartDataModel(subject="Soft Skills", student=round(soft_readiness, 1), required=85.0)
        ]

        # Learning Timeline
        learning_timeline = [
            LearningTimelineStepModel(
                stepNumber=1,
                phase="Foundation & Core Gaps",
                title="Bridge High Priority Technical Gaps",
                skillsCovered=[g.skill_name for g in skill_gaps if g.priority == "HIGH"][:3],
                estimatedWeeks=max(2, est_months * 2)
            ),
            LearningTimelineStepModel(
                stepNumber=2,
                phase="Practical Implementation",
                title="Build Portfolio Capstone Project",
                skillsCovered=[g.skill_name for g in skill_gaps if g.category == "PROJECT" or g.status in ["MISSING", "WEAK"]][3:6],
                estimatedWeeks=max(3, est_months * 3)
            ),
            LearningTimelineStepModel(
                stepNumber=3,
                phase="Certification & Interview Prep",
                title="Industry Certification & Mock Interview Mastery",
                skillsCovered=["Interview Preparation", "Resume Optimization", "Coding Practice"],
                estimatedWeeks=max(2, est_months * 2)
            )
        ]

        visual_analytics = VisualAnalyticsModel(
            radarChart=radar_chart,
            skillHeatmap=heatmap_items,
            priorityMatrix=priority_matrix_items,
            learningTimeline=learning_timeline
        )

        # Smart Insights
        smart_insights = [
            SmartInsightModel(
                title="Strong Base in Core Technicals",
                description=f"You already possess {round(tech_readiness, 0)}% of the required technical skills for {target_career}.",
                insightType="POSITIVE"
            ),
            SmartInsightModel(
                title="Priority Skill Focus",
                description=f"Prioritize {high_gaps[0].skill_name if high_gaps else 'Data Structures'} before applying for senior developer roles.",
                insightType="ACTIONABLE"
            ),
            SmartInsightModel(
                title="Project Readiness Boost",
                description="Completing the recommended Capstone Project will increase overall readiness by ~15%.",
                insightType="TIP"
            ),
            SmartInsightModel(
                title="Target Completion Timeline",
                description=f"With ~10 hours/week dedicated effort, you can reach 90%+ job readiness in {est_months} months.",
                insightType="POSITIVE"
            )
        ]

        return SkillGapAiResponseModel(
            targetCareer=target_career,
            overallReadinessScore=overall_readiness,
            overallGapPercentage=overall_gap_pct,
            estimatedJobReadinessMonths=est_months,
            readinessBreakdown=readiness_breakdown,
            skillGaps=skill_gaps,
            improvementRecommendations=recommendations,
            visualAnalytics=visual_analytics,
            smartInsights=smart_insights
        )
