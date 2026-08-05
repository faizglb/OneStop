from __future__ import annotations

from typing import Optional
from chatbot.agents.base_agent import BaseAgent
from chatbot.models import StudentContext
from chatbot.prompt_templates import build_roadmap_agent_prompt
from chatbot.vector_store import COLLECTION_SKILL, COLLECTION_CAREER, COLLECTION_GENERAL


class RoadmapAgent(BaseAgent):
    """
    Agent responsible for Learning Plans, Study Schedules, Milestones,
    and Progress Tracking.
    """

    def __init__(self):
        super().__init__(
            name="Roadmap Agent",
            description="Specialises in personalized learning plans, skill roadmaps, milestone tracking, and step-by-step career preparation schedules.",
            collections=[COLLECTION_SKILL, COLLECTION_CAREER, COLLECTION_GENERAL],
        )

    def build_prompt(
        self,
        query: str,
        context_window: str,
        retrieved_context: str,
        student_context: Optional[StudentContext] = None,
    ) -> str:
        return build_roadmap_agent_prompt(
            query=query,
            context_window=context_window,
            retrieved_context=retrieved_context,
            student_context=student_context,
        )
