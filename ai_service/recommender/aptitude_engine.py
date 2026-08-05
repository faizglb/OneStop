from typing import Dict, Any
from models import AiServiceRequestModel

class AptitudeEngine:
    @staticmethod
    def calculate_match(career: Dict[str, Any], student: AiServiceRequestModel) -> float:
        """
        Calculates alignment between student aptitude category scores and career prerequisites.
        Returns a score between 0.0 and 1.0.
        """
        prereqs = career.get("aptitude_alignment", {})
        scores = student.aptitude_scores
        
        if not prereqs or not scores:
            return 0.5
            
        total_weight = 0.0
        weighted_score = 0.0
        
        for category, threshold in prereqs.items():
            # Aptitude score percentage from backend is 0-100. Scale to 0-1.
            student_score = scores.get(category, 50.0) / 100.0
            
            # The higher the required threshold in career, the higher its weight in calculating the match
            weight = threshold
            total_weight += weight
            
            # If student matches or exceeds threshold, get full points. Otherwise scale proportionally.
            if student_score >= threshold:
                weighted_score += weight
            else:
                weighted_score += (student_score / threshold) * weight
                
        if total_weight == 0.0:
            return 0.5
            
        return weighted_score / total_weight
