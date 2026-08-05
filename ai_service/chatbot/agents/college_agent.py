from __future__ import annotations

from typing import Optional
from chatbot.agents.base_agent import BaseAgent
from chatbot.models import StudentContext
from chatbot.prompt_templates import build_college_agent_prompt
from chatbot.vector_store import COLLECTION_COLLEGE, COLLECTION_EXAM, COLLECTION_GENERAL


class CollegeAgent(BaseAgent):
    """
    Agent responsible for College Search, Cutoffs, Admissions, Placements,
    and College Comparisons.
    """

    def __init__(self):
        super().__init__(
            name="College Agent",
            description="Specialises in university/college selection, entrance exam cutoffs, placement statistics, and admission counselling.",
            collections=[COLLECTION_COLLEGE, COLLECTION_EXAM, COLLECTION_GENERAL],
        )

    def build_prompt(
        self,
        query: str,
        context_window: str,
        retrieved_context: str,
        student_context: Optional[StudentContext] = None,
    ) -> str:
        return build_college_agent_prompt(
            query=query,
            context_window=context_window,
            retrieved_context=retrieved_context,
            student_context=student_context,
        )
