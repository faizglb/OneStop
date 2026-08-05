from __future__ import annotations

import logging
from abc import ABC, abstractmethod
from typing import List, Optional, Any

from chatbot.models import AgentResponse, StudentContext
from chatbot.vector_store import RetrievedChunk, VectorStore, get_vector_store

logger = logging.getLogger("chatbot.agent")


class BaseAgent(ABC):
    """
    Abstract Base Class for all specialised AI Career Chatbot agents.
    Defines the contract for chunk retrieval, prompt construction, and answering.
    """

    def __init__(self, name: str, description: str, collections: List[str]):
        self.name = name
        self.description = description
        self.collections = collections
        self.vector_store: VectorStore = get_vector_store()

    @abstractmethod
    def build_prompt(
        self,
        query: str,
        context_window: str,
        retrieved_context: str,
        student_context: Optional[StudentContext] = None,
    ) -> str:
        """Construct the agent-specific prompt using the prompt template."""
        pass

    def retrieve_chunks(
        self, query_embedding: List[float], k: int = 5, score_threshold: float = 0.2
    ) -> List[RetrievedChunk]:
        """Retrieve relevant chunks across the agent's target vector collections."""
        return self.vector_store.multi_collection_search(
            query_embedding=query_embedding,
            collections=self.collections,
            k_per_collection=k,
            score_threshold=score_threshold,
        )

    def format_retrieved_context(self, chunks: List[RetrievedChunk]) -> str:
        """Format retrieved chunks into a text block for prompt injection."""
        if not chunks:
            return ""
        formatted_blocks = []
        for i, chunk in enumerate(chunks, 1):
            formatted_blocks.append(
                f"--- Knowledge Source #{i} [{chunk.source}] (Relevance: {int(chunk.relevance_score * 100)}%) ---\n{chunk.text}"
            )
        return "\n\n".join(formatted_blocks)

    def calculate_confidence(self, chunks: List[RetrievedChunk]) -> float:
        """Calculate overall response confidence based on retrieval scores."""
        if not chunks:
            return 0.5  # Base confidence when replying strictly from model internal knowledge + student profile
        top_score = max(chunk.relevance_score for chunk in chunks)
        avg_score = sum(chunk.relevance_score for chunk in chunks) / len(chunks)
        confidence = (top_score * 0.7) + (avg_score * 0.3)
        return round(min(max(confidence, 0.4), 0.98), 2)
