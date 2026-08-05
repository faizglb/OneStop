from typing import Dict, Any, List, Tuple
from models import AiServiceRequestModel

class SkillEngine:
    @staticmethod
    def calculate_match(career: Dict[str, Any], student: AiServiceRequestModel) -> Tuple[float, List[Dict[str, Any]]]:
        """
        Calculates skill match percentage and returns recommended skills.
        Returns:
            - match_score: float (0.0 to 1.0)
            - recommended_skills: list of dictionaries representing skills to learn
        """
        career_skills = [s.lower() for s in career.get("skills", [])]
        student_skills = [s.lower() for s in student.current_skills]
        
        if not career_skills:
            return 1.0, []
            
        matched_skills = set(career_skills).intersection(set(student_skills))
        missing_skills = set(career_skills) - matched_skills
        
        match_score = len(matched_skills) / len(career_skills)
        
        # Formulate recommended skills list with priority and confidence
        recommended_skills = []
        for skill in missing_skills:
            # Check if student already lists it as goal to mark as HIGH priority
            is_in_goals = any(skill in g.lower() for g in student.career_goals)
            priority = "HIGH" if is_in_goals else "MEDIUM"
            
            # Capitalize properly based on career casing
            display_name = next((s for s in career.get("skills", []) if s.lower() == skill), skill)
            
            # Confidence is derived from aptitude matching or baseline
            confidence = 80.0 if priority == "HIGH" else 65.0
            
            recommended_skills.append({
                "skillName": display_name,
                "priority": priority,
                "confidence": confidence
            })
            
        return match_score, recommended_skills
