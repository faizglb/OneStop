from __future__ import annotations

from typing import List, Optional, Dict, Any
from pydantic import BaseModel, Field, field_validator
import time


class StudentContext(BaseModel):
    """
    Aggregated snapshot of student data from Sprints 2–10.
    Passed from Spring Boot to give the chatbot personalised context.
    """
    student_name: Optional[str] = None
    career_goal: Optional[str] = None
    branch: Optional[str] = None
    education_level: Optional[str] = None
    current_cgpa: Optional[float] = None
    assessment_summary: Optional[str] = None   # e.g., "Aptitude: 78%, Personality: Analytical"
    recommended_career: Optional[str] = None    # top career from Sprint 4
    recommended_college: Optional[str] = None   # top college from Sprint 5
    recommended_scholarship: Optional[str] = None  # top scholarship from Sprint 6
    roadmap_summary: Optional[str] = None       # current roadmap phase + progress %
    skill_gap_summary: Optional[str] = None     # missing skills + readiness %
    resume_score: Optional[float] = None        # ATS score from Sprint 9
    resume_summary: Optional[str] = None        # key findings from resume analysis
    skills: List[str] = Field(default_factory=list)
    interests: List[str] = Field(default_factory=list)
    family_income: Optional[str] = None
    category: Optional[str] = None             # General, OBC, SC, ST


class SourceCitation(BaseModel):
    """A single retrieved source chunk with metadata."""
    document_title: str
    source_type: str                  # "Knowledge Base", "Career Data", "College Data", etc.
    chunk_text: str
    relevance_score: float = Field(ge=0.0, le=1.0)
    knowledge_source: str             # collection name


class ChatRequest(BaseModel):
    """Incoming chat request from Spring Boot."""
    session_id: str
    student_id: str
    message: str = Field(min_length=1, max_length=2000)
    student_context: Optional[StudentContext] = None

    @field_validator("message")
    @classmethod
    def strip_message(cls, v: str) -> str:
        return v.strip()


class ChatResponse(BaseModel):
    """Outgoing chat response to Spring Boot."""
    response: str
    agent_used: str
    sources: List[SourceCitation] = Field(default_factory=list)
    confidence_score: float = Field(ge=0.0, le=1.0, default=0.7)
    session_id: str
    timestamp: float = Field(default_factory=time.time)
    is_personalized: bool = False


class InitSessionRequest(BaseModel):
    """Request to initialise a new chat session in memory."""
    session_id: str
    student_id: str
    student_context: Optional[StudentContext] = None


class InitSessionResponse(BaseModel):
    session_id: str
    status: str = "INITIALIZED"
    message: str = "Chat session initialised successfully."


class AgentResponse(BaseModel):
    """Internal response from an AI agent before packaging."""
    answer: str
    agent_name: str
    collections_used: List[str] = Field(default_factory=list)
    retrieved_chunks: List[Any] = Field(default_factory=list)  # List[RetrievedChunk]
    confidence: float = 0.7
    is_personalized: bool = False
