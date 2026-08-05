from typing import List
from models import ScholarshipAiRequestModel, ScholarshipRecDtoModel # wait! Import the correct models!
from models import ScholarshipAiRequestModel, ScholarshipRecDtoModel
from models import ScholarshipAiRequestModel, ScholarshipRecDtoModel
from models import LearningAiRequestModel, LearningAiResponseModel, LearningRecDtoModel

class LearningRecommendationPipeline:
    @staticmethod
    def get_recommendations(request: LearningAiRequestModel) -> List[LearningRecDtoModel]:
        """
        Computes match priority, confidence scores, and reasons for learning resources.
        Rules evaluated:
          - Career roadmap tag alignment (Sprint 4 data)
          - Diagnostic assessment score deficiencies (Aptitude scores < 80% maps high priority on programming/DSA)
          - Level alignment (Beginner matching low CGPA, Advanced matching high CGPA/experience)
        """
        recs = []
        
        for resource in request.resources:
            priority = "MEDIUM"
            confidence = 70.0
            reasons = []
            
            skill_lower = resource.skill.lower()
            category_lower = resource.category.lower()
            
            # ── 1. Career Roadmap check (Sprint 4 matches) ──
            career_goals_lower = [g.lower() for g in request.career_goals]
            recommended_careers_lower = [c.lower() for c in request.recommended_careers]
            
            # Software/Tech path alignment
            is_tech_career = any("software" in g or "developer" in g or "engineer" in g or "data" in g or "ai" in g for g in career_goals_lower + recommended_careers_lower)
            
            if is_tech_career:
                if category_lower in ["programming languages", "frameworks", "dsa", "system design"]:
                    priority = "HIGH"
                    confidence += 15.0
                    reasons.append(f"Highly recommended to support your target technical career track in {resource.skill}")
            
            # Business/Management path alignment
            is_biz_career = any("finance" in g or "manager" in g or "business" in g for g in career_goals_lower + recommended_careers_lower)
            if is_biz_career:
                if category_lower in ["soft skills", "communication"]:
                    priority = "HIGH"
                    confidence += 15.0
                    reasons.append(f"Supports leadership skills development required for business and management careers")

            # ── 2. Diagnostic Assessment Scores Deficiency check ──
            # If programming fundamentals or logical reasoning percentage is lower, highlight basic programming and DSA
            prog_score = request.aptitude_scores.get("Programming Fundamentals", 100.0)
            logic_score = request.aptitude_scores.get("Logical Reasoning", 100.0)
            
            if (prog_score < 80.0 or logic_score < 80.0) and category_lower in ["programming languages", "dsa"]:
                priority = "HIGH"
                confidence += 10.0
                reasons.append(f"Targeted to reinforce basic skill gaps identified in your coding/logical diagnostic results")

            # ── 3. Academic Level Alignment check ──
            cgpa = request.current_cgpa
            diff_lower = resource.difficulty.lower()
            
            if diff_lower == "beginner" and cgpa < 7.5:
                confidence += 5.0
                reasons.append("Beginner-friendly structure matches your current preparation level")
            elif diff_lower == "advanced" and cgpa >= 8.5:
                confidence += 5.0
                reasons.append("Advanced syllabus provides the competitive edge suitable for your academic strengths")

            # Final bindings
            final_confidence = min(100.0, max(30.0, confidence))
            
            # Simple duration completing estimation
            duration_lower = resource.duration.lower()
            est_time = "1-2 weeks"
            if "hour" in duration_lower:
                try:
                    hours = int("".join(filter(str.isdigit, duration_lower)))
                    if hours > 40: est_time = "3-4 weeks"
                    elif hours > 10: est_time = "1-2 weeks"
                    else: est_time = "3-5 days"
                except:
                    pass
            elif "week" in duration_lower:
                est_time = duration_lower

            reason_txt = " • ".join(reasons[:3]) + "."
            if not reasons:
                reason_txt = f"Develops core foundational competencies in {resource.skill}."

            recs.append(LearningRecDtoModel(
                resourceId=str(resource.resource_id),
                recommendedSkill=resource.skill,
                priority=priority,
                confidenceScore=round(final_confidence, 2),
                reason=reason_txt,
                estimatedCompletionTime=est_time
            ))
            
        # Sort by confidence score descending
        recs.sort(key=lambda r: r.confidence_score, reverse=True)
        return recs
