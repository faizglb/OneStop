from typing import List, Dict, Any
from models import AiServiceRequestModel

class RuleEngine:
    @staticmethod
    def match_career(career: Dict[str, Any], student: AiServiceRequestModel) -> float:
        """
        Calculate eligibility or baseline compatibility based on academic fields and branches.
        Returns a score between 0.0 and 1.0.
        """
        domain = career.get("domain", "").lower()
        branch = student.branch.lower()
        course = student.current_course.lower()
        
        # Technology domains favor Computer Science/IT/Software Engineering
        if domain == "technology":
            tech_keywords = ["computer", "it", "information", "software", "ece", "electronics", "technology", "artificial", "data"]
            if any(kw in branch for kw in tech_keywords) or any(kw in course for kw in tech_keywords):
                return 1.0
            return 0.4  # Allowed but lower priority
            
        # Finance domains favor Commerce/Finance/Economics/MBA/Management
        elif domain == "finance":
            finance_keywords = ["finance", "commerce", "bcom", "bba", "mba", "economics", "accounting", "business"]
            if any(kw in branch for kw in finance_keywords) or any(kw in course for kw in finance_keywords):
                return 1.0
            return 0.3
            
        # Design domains favor HCI, Design, Fine Arts, Psychology, CS
        elif domain == "design":
            design_keywords = ["design", "hci", "human", "fine arts", "graphics", "psychology", "architecture", "computer"]
            if any(kw in branch for kw in design_keywords) or any(kw in course for kw in design_keywords):
                return 1.0
            return 0.5
            
        # Management domains are broadly compatible, but business backgrounds get a boost
        elif domain == "management":
            biz_keywords = ["business", "management", "mba", "bba", "industrial", "admin"]
            if any(kw in branch for kw in biz_keywords) or any(kw in course for kw in biz_keywords):
                return 1.0
            return 0.7
            
        return 0.5
