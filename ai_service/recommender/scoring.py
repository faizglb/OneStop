from typing import Dict, Any
from models import AiServiceRequestModel
from config import Config

class ScoringEngine:
    @staticmethod
    def compute_scores(
        rule_score: float,
        skill_score: float,
        interest_score: float,
        personality_score: float,
        aptitude_score: float,
        student: AiServiceRequestModel,
        career: Dict[str, Any]
    ) -> Dict[str, float]:
        """
        Applies configurable weights to match scores to compute:
          - match_score: float (0.0 to 1.0)
          - confidence_score: float (0.0 to 1.0)
        
        Configured weights:
          - Assessments: 40% (Aptitude/Personality/Interest)
          - Skills: 30%
          - Declared Interests: 20%
          - Career Goals: 10%
        """
        # 1. Assessment Score Component (Aptitude, Personality, interest match scores)
        # 50% Aptitude, 25% Personality, 25% Holland interest match
        assessments_component = (
            aptitude_score * Config.WEIGHT_APTITUDE +
            personality_score * Config.WEIGHT_PERSONALITY +
            interest_score * Config.WEIGHT_RIASEC
        )
        
        # 2. Skills Component
        skills_component = skill_score
        
        # 3. Declared Interests Component
        # Checks if career sector/domain matches student interests list
        declared_interests = [i.lower() for i in student.interests]
        career_domain = career.get("domain", "").lower()
        career_sector = career.get("sector", "").lower()
        
        interest_match = 0.0
        if any(i in career_domain or i in career_sector for i in declared_interests):
            interest_match = 1.0
        elif declared_interests:
            # Partial matching by skill keywords
            interest_match = 0.3
        else:
            interest_match = 0.5 # Default if none declared
            
        # 4. Career Goals Component
        # Checks if career name matches any of student's declared goals
        goals_match = 0.0
        career_name = career.get("career_name", "").lower()
        
        if any(career_name in g.lower() or g.lower() in career_name for g in student.career_goals):
            goals_match = 1.0
        elif student.career_goals:
            goals_match = 0.2
        else:
            goals_match = 0.5
            
        # Overall weighted match score (0-1)
        match_score = (
            assessments_component * Config.WEIGHT_ASSESSMENTS +
            skills_component * Config.WEIGHT_SKILLS +
            interest_match * Config.WEIGHT_INTERESTS +
            goals_match * Config.WEIGHT_GOALS
        )
        
        # Confidence score incorporates rule filters and overall CGPA performance
        # Eligible students get boosted confidence
        gpa_booster = min(1.0, student.current_cgpa / 10.0)
        confidence_score = (match_score * 0.7) + (rule_score * 0.2) + (gpa_booster * 0.1)
        
        return {
            "match_score": min(1.0, max(0.0, match_score)),
            "confidence_score": min(1.0, max(0.0, confidence_score))
        }
