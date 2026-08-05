from typing import List, Dict
from datetime import datetime, timedelta
from models import RoadmapAiRequestModel, RoadmapAiResponseModel, PhaseAiDtoModel, TaskAiDtoModel, MilestoneAiDtoModel

class RoadmapGeneratorPipeline:
    @staticmethod
    def generate_roadmap(request: RoadmapAiRequestModel) -> RoadmapAiResponseModel:
        """
        Dynamically constructs an 8-phase personalized career learning roadmap based on target career,
        weekly hours, diagnostic skill gaps, and available Sprint 7 learning resources.
        """
        target = request.target_career
        start_date = datetime.strptime(request.start_date, "%Y-%m-%d") if isinstance(request.start_date, str) else request.start_date
        
        # Calculate Duration Days
        duration_days = 60
        dur_lower = request.duration.lower()
        if "30" in dur_lower: duration_days = 30
        elif "60" in dur_lower: duration_days = 60
        elif "90" in dur_lower: duration_days = 90
        elif "6-month" in dur_lower or "180" in dur_lower: duration_days = 180
        elif "1-year" in dur_lower or "365" in dur_lower: duration_days = 365
        
        end_date = start_date + timedelta(days=duration_days)

        # Map Sprint 7 resources by skill/category for quick linking
        resource_map: Dict[str, str] = {}
        for r in request.available_resources:
            key = r.skill.lower()
            resource_map[key] = str(r.id)

        # Phase templates
        phase_names = [
            "Phase 1: Foundation",
            "Phase 2: Core Skills",
            "Phase 3: Projects",
            "Phase 4: Advanced Skills",
            "Phase 5: Portfolio Building",
            "Phase 6: Resume Preparation",
            "Phase 7: Interview Preparation",
            "Phase 8: Job Ready Checklist"
        ]

        days_per_phase = max(1, duration_days // 8)
        current_task_date = start_date

        phases: List[PhaseAiDtoModel] = []

        # Default tech stack tasks
        skills_by_phase = {
            1: [("Computer Science Basics", "FOUNDATION", "computer science"), ("Syntax & Logic Fundamentals", "FOUNDATION", "java")],
            2: [("Object Oriented Programming & Data Structures", "CORE_SKILL", "python"), ("Database Systems & SQL", "CORE_SKILL", "sql")],
            3: [("Mini Project: Interactive Web Application", "PROJECT", "react"), ("REST API Integration", "PROJECT", "java")],
            4: [("Software Architecture & System Design", "CORE_SKILL", "software engineering"), ("Cloud Deployment Basics", "CORE_SKILL", "cloud")],
            5: [("Cap-stone Full Stack Portfolio Project", "PROJECT", "react"), ("GitHub Code Documentation & CI/CD", "PROJECT", "github")],
            6: [("ATS Resume Formatting & Impact Statements", "REVISION", "software engineering"), ("LinkedIn Profile Optimization", "REVISION", "soft skills")],
            7: [("LeetCode Coding Challenges & DSA Mock Interviews", "INTERVIEW_PREP", "python"), ("Behavioral Interview Preparation", "INTERVIEW_PREP", "soft skills")],
            8: [("Final Technical Checklist Audit & Job Applications", "REVISION", "software engineering")]
        }

        for seq in range(1, 9):
            phase_name = phase_names[seq - 1]
            phase_tasks: List[TaskAiDtoModel] = []
            
            task_defs = skills_by_phase.get(seq, [("General Competency Module", "CORE_SKILL", "java")])
            
            for title, t_type, skill_key in task_defs:
                linked_id = resource_map.get(skill_key, None)
                
                phase_tasks.append(TaskAiDtoModel(
                    title=f"{title} for {target}",
                    description=f"Complete hands-on modules in {title} to build required industry skills.",
                    taskType=t_type,
                    estimatedHours=max(1, request.weekly_hours // 3),
                    dueDate=current_task_date.strftime("%Y-%m-%d"),
                    priority="HIGH" if seq in [1, 2, 7] else "MEDIUM",
                    linkedResourceId=linked_id
                ))
                current_task_date += timedelta(days=max(1, days_per_phase // len(task_defs)))

            phases.append(PhaseAiDtoModel(
                phaseName=phase_name,
                sequence=seq,
                description=f"Structured milestones for {phase_name} in your {target} roadmap.",
                tasks=phase_tasks
            ))

        # Milestones
        milestones = [
            MilestoneAiDtoModel(
                title="Foundational Competency Certified",
                description="Complete basic programming and core syntax assessments.",
                completionCriteria="Phase 1 & Phase 2 tasks 100% completed."
            ),
            MilestoneAiDtoModel(
                title="Portfolio & Project Deployed",
                description="Deploy at least one full-stack project live on GitHub/Cloud.",
                completionCriteria="Phase 3 & Phase 5 tasks completed."
            ),
            MilestoneAiDtoModel(
                title="Job & Interview Ready",
                description="Final resume reviewed and mock technical interview passed.",
                completionCriteria="Phase 6, 7 & 8 checklist completed."
            )
        ]

        title = f"AI Personalized {target} Roadmap ({request.duration})"
        if request.target_company:
            title += f" — Target: {request.target_company}"

        return RoadmapAiResponseModel(
            title=title,
            endDate=end_date.strftime("%Y-%m-%d"),
            phases=phases,
            milestones=milestones
        )
