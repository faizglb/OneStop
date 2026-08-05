from __future__ import annotations

from typing import Optional
from chatbot.agents.base_agent import BaseAgent
from chatbot.models import StudentContext
from chatbot.prompt_templates import build_resume_agent_prompt
from chatbot.vector_store import COLLECTION_RESUME, COLLECTION_GENERAL


class ResumeAgent(BaseAgent):
    """
    Agent responsible for Resume Review, ATS Optimization, Project Descriptions,
    and CV Improvements.
    """

    def __init__(self):
        super().__init__(
            name="Resume Agent",
            description="Specialises in resume analysis, ATS optimization, bullet point enhancement, and project presentation for job applications.",
            collections=[COLLECTION_RESUME, COLLECTION_GENERAL],
        )

    def build_prompt(
        self,
        query: str,
        context_window: str,
        retrieved_context: str,
        student_context: Optional[StudentContext] = None,
    ) -> str:
        return build_resume_agent_prompt(
            query=query,
            context_window=context_window,
            retrieved_context=retrieved_context,
            student_context=student_context,
        )
