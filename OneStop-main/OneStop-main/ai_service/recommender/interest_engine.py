import math
from typing import Dict, Any
from models import AiServiceRequestModel

class InterestEngine:
    @staticmethod
    def calculate_match(career: Dict[str, Any], student: AiServiceRequestModel) -> float:
        """
        Calculates similarity between student's RIASEC interest scores and career weights.
        Returns a score between 0.0 and 1.0.
        """
        career_weights = career.get("riasec_weights", {})
        student_profile = student.riasec_profile
        
        if not career_weights or not student_profile:
            return 0.5
            
        dot_product = 0.0
        norm_c = 0.0
        norm_s = 0.0
        
        for trait, ideal_val in career_weights.items():
            # Normalized score from backend is 0-100. Scale to 0-1.
            student_val = student_profile.get(trait, 50.0) / 100.0
            
            dot_product += ideal_val * student_val
            norm_c += ideal_val ** 2
            norm_s += student_val ** 2
            
        if norm_c == 0.0 or norm_s == 0.0:
            return 0.5
            
        similarity = dot_product / (math.sqrt(norm_c) * math.sqrt(norm_s))
        
        # Scale to emphasize high interest alignment
        scaled_score = max(0.0, (similarity - 0.4) / 0.6)
        return min(1.0, scaled_score)
