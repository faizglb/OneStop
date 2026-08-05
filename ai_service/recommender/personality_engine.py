import math
from typing import Dict, Any
from models import AiServiceRequestModel

class PersonalityEngine:
    @staticmethod
    def calculate_match(career: Dict[str, Any], student: AiServiceRequestModel) -> float:
        """
        Calculates cosine similarity between student's Big Five traits and career's ideal profile.
        Returns a score between 0.0 and 1.0.
        """
        career_profile = career.get("ocean_profile", {})
        student_profile = student.personality_traits
        
        if not career_profile or not student_profile:
            return 0.5  # Neutral default
            
        # Standardize keys (personalityTraits uses OPENNESS, CONSCIENTIOUSNESS etc)
        dot_product = 0.0
        norm_c = 0.0
        norm_s = 0.0
        
        for trait, ideal_val in career_profile.items():
            # Normalized score from backend is 0-100. Scale to 0-1 for similarity check.
            student_val = student_profile.get(trait, 50.0) / 100.0
            
            dot_product += ideal_val * student_val
            norm_c += ideal_val ** 2
            norm_s += student_val ** 2
            
        if norm_c == 0.0 or norm_s == 0.0:
            return 0.5
            
        similarity = dot_product / (math.sqrt(norm_c) * math.sqrt(norm_s))
        
        # Scale range from [0.5, 1.0] to [0.0, 1.0] for career matching sensitivity
        scaled_score = max(0.0, (similarity - 0.5) / 0.5)
        return min(1.0, scaled_score)
