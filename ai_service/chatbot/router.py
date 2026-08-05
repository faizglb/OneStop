from __future__ import annotations

import logging
import re
from typing import Dict, Tuple

from chatbot.agents.base_agent import BaseAgent
from chatbot.agents.career_agent import CareerAgent
from chatbot.agents.college_agent import CollegeAgent
from chatbot.agents.roadmap_agent import RoadmapAgent
from chatbot.agents.scholarship_agent import ScholarshipAgent
from chatbot.agents.resume_agent import ResumeAgent
from chatbot.agents.skill_agent import SkillAgent

logger = logging.getLogger("chatbot.router")


class AgentRouter:
    """
    Intelligently routes incoming student queries to the most appropriate AI agent.
    Combines rule-based keyword matching (fast, 0-cost) with LLM intent classification fallback.
    """

    def __init__(self):
        # Instantiate agents
        self.career_agent = CareerAgent()
        self.college_agent = CollegeAgent()
        self.scholarship_agent = ScholarshipAgent()
        self.roadmap_agent = RoadmapAgent()
        self.resume_agent = ResumeAgent()
        self.skill_agent = SkillAgent()

        # Keyword mapping rules
        self.keyword_rules: Dict[BaseAgent, Tuple[str, ...]] = {
            self.college_agent: (
                "college", "university", "institute", "iit", "nit", "iiit", "bits", "du", "iim",
                "cutoff", "jee", "neet", "bit sat", "josaa", "admission", "campus", "rank", "percentile",
                "branch", "seat", "fees", "hostel", "placement", "counselling"
            ),
            self.scholarship_agent: (
                "scholarship", "nsp", "financial aid", "stipend", "grant", "fee waiver", "fellowship",
                "eligibility", "income certificate", "post matric", "aicte pragati", "inspire scholarship"
            ),
            self.resume_agent: (
                "resume", "cv", "ats", "ats score", "bullet points", "portfolio", "cover letter",
                "interview", "linkedin profile", "projects section", "resume review"
            ),
            self.skill_agent: (
                "skill", "skill gap", "missing skills", "what skills", "readiness", "certification",
                "learn python", "learn java", "dsa", "sql", "framework", "course suggestion"
            ),
            self.roadmap_agent: (
                "roadmap", "learning plan", "milestone", "schedule", "study plan", "where to start",
                "step by step", "how to become", "months plan", "timeline", "week"
            ),
            self.career_agent: (
                "career", "salary", "job", "future scope", "lpa", "ctc", "role", "data scientist",
                "software engineer", "devops", "scope", "field", "demand", "industry", "work mode"
            ),
        }

    def route(self, query: str) -> Tuple[BaseAgent, float]:
        """
        Routes the query to an agent. Returns (selected_agent, confidence_score).
        """
        query_lower = query.lower()

        # Phase 1: Rule-Based Keyword Scoring
        scores: Dict[BaseAgent, int] = {agent: 0 for agent in self.keyword_rules}

        for agent, keywords in self.keyword_rules.items():
            for kw in keywords:
                if re.search(rf"\b{re.escape(kw)}\b", query_lower):
                    scores[agent] += 2
                elif kw in query_lower:
                    scores[agent] += 1

        best_agent = max(scores, key=lambda a: scores[a])
        highest_score = scores[best_agent]

        if highest_score > 0:
            confidence = min(0.6 + (highest_score * 0.1), 0.95)
            logger.info(f"Routed query via keyword match → {best_agent.name} (Score: {highest_score}, Confidence: {confidence})")
            return best_agent, round(confidence, 2)

        # Phase 2: Fallback to CareerAgent as general mentor
        logger.info(f"No explicit keyword match → Defaulting to CareerAgent")
        return self.career_agent, 0.70


# Singleton router
_agent_router: AgentRouter | None = None


def get_agent_router() -> AgentRouter:
    global _agent_router
    if _agent_router is None:
        _agent_router = AgentRouter()
    return _agent_router
