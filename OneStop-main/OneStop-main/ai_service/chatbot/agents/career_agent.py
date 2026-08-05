from __future__ import annotations

from typing import Optional
from chatbot.agents.base_agent import BaseAgent
from chatbot.models import StudentContext
from chatbot.prompt_templates import build_career_agent_prompt
from chatbot.vector_store import COLLECTION_CAREER, COLLECTION_SKILL, COLLECTION_GENERAL


class CareerAgent(BaseAgent):
    """
    Agent responsible for Career Guidance, Salary Trends, Future Scope,
    and Career Comparisons.
    """

    def __init__(self):
        super().__init__(
            name="Career Agent",
            description="Specialises in career paths, job roles, salary information, future scope, and career comparisons.",
            collections=[COLLECTION_CAREER, COLLECTION_SKILL, COLLECTION_GENERAL],
        )

    def build_prompt(
        self,
        query: str,
        context_window: str,
        retrieved_context: str,
        student_context: Optional[StudentContext] = None,
    ) -> str:
        return build_career_agent_prompt(
            query=query,
            context_window=context_window,
            retrieved_context=retrieved_context,
            student_context=student_context,
        )
