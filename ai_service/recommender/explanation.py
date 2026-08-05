from typing import Dict, Any, List
from models import AiServiceRequestModel

class ExplanationGenerator:
    @staticmethod
    def generate_reason(
        career: Dict[str, Any],
        student: AiServiceRequestModel,
        skill_score: float,
        interest_score: float,
        personality_score: float,
        aptitude_score: float
    ) -> str:
        """
        Generates a human-readable explanation of why this career role was recommended
        based on the highest matching scores across components.
        """
        career_name = career.get("career_name")
        reasons = []
        
        # 1. Aptitude alignment
        if aptitude_score >= 0.8:
            reasons.append(f"your exceptional performance in the aptitude assessments matching cognitive demands for {career_name}")
            
        # 2. Skill similarity
        if skill_score >= 0.7:
            reasons.append("your existing technical and programming skill background aligns strongly with the requirements")
        elif skill_score >= 0.4:
            reasons.append("you possess foundational skills that form a solid base for this role")
            
        # 3. Interest profile (RIASEC)
        if interest_score >= 0.8:
            reasons.append("your Holland Interest profile shows a natural cognitive preference for activities in this field")
            
        # 4. Personality trait (OCEAN)
        if personality_score >= 0.8:
            reasons.append("your personality traits match the professional environment and work style of this role")
            
        # 5. Career goals declared
        career_lower = career_name.lower()
        if any(career_lower in g.lower() or g.lower() in career_lower for g in student.career_goals):
            reasons.append("this directly aligns with your declared career aspirations and future goals")
            
        if not reasons:
            reasons.append("your academic background and diagnostic assessments indicate a balanced compatibility for this career pathway")
            
        # Combine reasons into a readable string
        explanation = f"Recommended based on " + ", and ".join(reasons[:3]) + "."
        return explanation
