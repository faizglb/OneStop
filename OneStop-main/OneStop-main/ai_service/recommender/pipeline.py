from typing import List, Dict, Any
from models import AiServiceRequestModel, CareerRecommendationModel, RecommendedSkillModel, RecommendedCertificationModel, RecommendedProjectModel
from recommender.dataset import CAREERS_DATASET
from recommender.rule_engine import RuleEngine
from recommender.skill_engine import SkillEngine
from recommender.personality_engine import PersonalityEngine
from recommender.aptitude_engine import AptitudeEngine
from recommender.interest_engine import InterestEngine
from recommender.scoring import ScoringEngine
from recommender.explanation import ExplanationGenerator

class RecommendationPipeline:
    @staticmethod
    def get_recommendations(student: AiServiceRequestModel) -> List[CareerRecommendationModel]:
        """
        Orchestrates the entire recommendation pipeline:
          1. Rule eligibility filters.
          2. Matching metrics (skills, personality, interest, aptitude).
          3. Scoring & ranking.
          4. Output DTO construction.
        """
        raw_recommendations = []
        
        for career in CAREERS_DATASET:
            # 1. Rules Baseline Match
            rule_score = RuleEngine.match_career(career, student)
            
            # 2. Skill match
            skill_score, missing_skills = SkillEngine.calculate_match(career, student)
            
            # 3. Personality trait match (OCEAN)
            personality_score = PersonalityEngine.calculate_match(career, student)
            
            # 4. Aptitude match
            aptitude_score = AptitudeEngine.calculate_match(career, student)
            
            # 5. RIASEC interest match
            interest_score = InterestEngine.calculate_match(career, student)
            
            # 6. Compute scores
            scores = ScoringEngine.compute_scores(
                rule_score=rule_score,
                skill_score=skill_score,
                interest_score=interest_score,
                personality_score=personality_score,
                aptitude_score=aptitude_score,
                student=student,
                career=career
            )
            
            # 7. Generate reason
            reason = ExplanationGenerator.generate_reason(
                career=career,
                student=student,
                skill_score=skill_score,
                interest_score=interest_score,
                personality_score=personality_score,
                aptitude_score=aptitude_score
            )
            
            # Match scores are scaled to 0-100%
            confidence_pct = round(scores["confidence_score"] * 100.0, 2)
            match_pct = round(scores["match_score"] * 100.0, 2)
            
            # Map skills response models
            skills_models = [
                RecommendedSkillModel(
                    skillName=s["skillName"],
                    priority=s["priority"],
                    confidence=s["confidence"]
                ) for s in missing_skills
            ]
            
            # Map certifications response models
            certs_models = [
                RecommendedCertificationModel(
                    certificationName=c["name"],
                    provider=c["provider"],
                    confidence=c["confidence"]
                ) for c in career.get("certifications", [])
            ]
            
            # Map projects response models
            projects_models = [
                RecommendedProjectModel(
                    projectTitle=p["title"],
                    projectDescription=p["description"],
                    difficulty=p["difficulty"],
                    estimatedDuration=p["duration"]
                ) for p in career.get("projects", [])
            ]
            
            rec = CareerRecommendationModel(
                careerName=career["career_name"],
                careerDescription=career["description"],
                confidenceScore=confidence_pct,
                matchScore=match_pct,
                reason=reason,
                expectedSalary=career["expected_salary"],
                industryGrowth=career["industry_growth"],
                requiredDegree=career["required_degree"],
                futureScope=career["future_scope"],
                jobDemand=career["job_demand"],
                estimatedLearningTime=career["estimated_learning_time"],
                pros=career["pros"],
                challenges=career["challenges"],
                requiredSkills=skills_models,
                recommendedCertifications=certs_models,
                suggestedProjects=projects_models
            )
            
            raw_recommendations.append(rec)
            
        # Sort recommendations by confidence score descending
        raw_recommendations.sort(key=lambda r: r.confidence_score, reverse=True)
        
        # Return top 5 recommendations
        return raw_recommendations[:5]
