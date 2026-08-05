from __future__ import annotations

from typing import Optional
from chatbot.agents.base_agent import BaseAgent
from chatbot.models import StudentContext
from chatbot.prompt_templates import build_scholarship_agent_prompt
from chatbot.vector_store import COLLECTION_SCHOLARSHIP, COLLECTION_GENERAL


class ScholarshipAgent(BaseAgent):
    """
    Agent responsible for Scholarship Eligibility, Application Steps,
    Deadlines, and Benefits.
    """

    def __init__(self):
        super().__init__(
            name="Scholarship Agent",
            description="Specialises in government & private scholarships, eligibility verification, application processes, and deadlines.",
            collections=[COLLECTION_SCHOLARSHIP, COLLECTION_GENERAL],
        )

    def build_prompt(
        self,
        query: str,
        context_window: str,
        retrieved_context: str,
        student_context: Optional[StudentContext] = None,
    ) -> str:
        return build_scholarship_agent_prompt(
            query=query,
            context_window=context_window,
            retrieved_context=retrieved_context,
            student_context=student_context,
        )
