from __future__ import annotations

import logging
import os
from typing import List, Optional

from chatbot.config import chatbot_config
from chatbot.embeddings import get_embedding_service
from chatbot.memory import get_conversation_memory
from chatbot.models import ChatRequest, ChatResponse, SourceCitation, StudentContext
from chatbot.router import get_agent_router
from chatbot.security import ChatSecurity
from chatbot.vector_store import RetrievedChunk

logger = logging.getLogger("chatbot.rag")


class RAGPipeline:
    """
    Core RAG Pipeline orchestrating:
    1. Security Validation
    2. Embedding Generation
    3. Agent Routing
    4. Vector Store Context Retrieval
    5. Personalised Prompt Construction
    6. LLM Generation (LangChain / Gemini API)
    7. Citation Extraction & Response Formatting
    8. Conversation Memory Persistence
    """

    def __init__(self):
        self.embedding_service = get_embedding_service()
        self.agent_router = get_agent_router()
        self.memory = get_conversation_memory()
        self._llm = None

    def _get_llm(self):
        if self._llm is None:
            api_key = chatbot_config.gemini_api_key
            if not api_key:
                logger.warning("GEMINI_API_KEY environment variable not set. RAG pipeline running in fallback mode.")
                return None

            try:
                from langchain_google_genai import ChatGoogleGenerativeAI
                self._llm = ChatGoogleGenerativeAI(
                    model=chatbot_config.gemini_model,
                    google_api_key=api_key,
                    temperature=chatbot_config.temperature,
                    max_output_tokens=chatbot_config.max_output_tokens,
                    top_p=chatbot_config.top_p,
                )
                logger.info(f"Initialised LangChain ChatGoogleGenerativeAI ({chatbot_config.gemini_model})")
            except Exception as e:
                logger.error(f"Failed to initialise LangChain Gemini LLM: {e}")
                self._llm = None
        return self._llm

    def execute(self, request: ChatRequest) -> ChatResponse:
        """Execute complete RAG workflow for a user query."""
        # 1. Security Check
        is_valid, err_msg = ChatSecurity.validate_message(request.message, chatbot_config.max_message_length)
        if not is_valid:
            return ChatResponse(
                response=f"⚠️ {err_msg}",
                agent_used="Security Guard",
                sources=[],
                confidence_score=1.0,
                session_id=request.session_id,
            )

        # Record user message in memory
        self.memory.add_user_message(request.session_id, request.student_id, request.message)

        # 2. Agent Routing
        agent, route_confidence = self.agent_router.route(request.message)

        # 3. Vector Store Chunk Retrieval
        query_embedding = self.embedding_service.embed_query(request.message)
        chunks: List[RetrievedChunk] = agent.retrieve_chunks(
            query_embedding=query_embedding,
            k=chatbot_config.retrieval_top_k,
            score_threshold=chatbot_config.retrieval_score_threshold,
        )

        retrieved_text = agent.format_retrieved_context(chunks)
        calc_confidence = agent.calculate_confidence(chunks)

        # 4. Context Window from Memory
        context_window_text = self.memory.get_context_text(request.session_id, request.student_id)

        # 5. Build Agent Prompt
        prompt = agent.build_prompt(
            query=request.message,
            context_window=context_window_text,
            retrieved_context=retrieved_text,
            student_context=request.student_context,
        )

        # 6. Generate LLM Response
        llm = self._get_llm()
        if llm is not None:
            try:
                response_obj = llm.invoke(prompt)
                raw_answer = response_obj.content if hasattr(response_obj, "content") else str(response_obj)
            except Exception as e:
                logger.error(f"Gemini API invocation failed: {e}. Falling back to rule-based response synthesis.")
                raw_answer = self._generate_fallback_response(request.message, agent.name, chunks, request.student_context)
        else:
            raw_answer = self._generate_fallback_response(request.message, agent.name, chunks, request.student_context)

        # 7. Output Sanitisation
        final_answer = ChatSecurity.sanitize_output(raw_answer)

        # 8. Build Source Citations
        citations = []
        for chunk in chunks[:3]:  # Top 3 citations
            citations.append(
                SourceCitation(
                    document_title=chunk.source,
                    source_type="Knowledge Base",
                    chunk_text=chunk.text[:200] + "...",
                    relevance_score=chunk.relevance_score,
                    knowledge_source=chunk.collection,
                )
            )

        # 9. Record Assistant Response in Memory
        self.memory.add_assistant_message(request.session_id, request.student_id, final_answer, agent.name)

        is_personalized = request.student_context is not None and bool(
            request.student_context.career_goal or request.student_context.recommended_career
        )

        return ChatResponse(
            response=final_answer,
            agent_used=agent.name,
            sources=citations,
            confidence_score=calc_confidence,
            session_id=request.session_id,
            is_personalized=is_personalized,
        )

    def _generate_fallback_response(
        self,
        query: str,
        agent_name: str,
        chunks: List[RetrievedChunk],
        student_context: Optional[StudentContext],
    ) -> str:
        """
        Rule-based fallback response generator when LLM API is unavailable.
        Ensures system remains 100% operational even without external API keys.
        """
        greeting = ""
        if student_context and student_context.student_name:
            greeting = f"Hello {student_context.student_name}! "

        personal_note = ""
        if student_context and student_context.career_goal:
            personal_note = f"\n\n*Note based on your profile:* As your target goal is **{student_context.career_goal}**, keeping your skills aligned with this path is highly recommended."

        if chunks:
            top_chunk = chunks[0]
            return f"{greeting}Here is the information retrieved regarding your query:\n\n{top_chunk.text}{personal_note}\n\n*Answer generated by ACEA {agent_name} using Knowledge Base.*"

        return f"{greeting}Thank you for your question regarding **{query}**. As your career mentor, I recommend exploring our Assessment and Recommendation modules for detailed personalized advice.{personal_note}"


# Singleton RAG Pipeline
_rag_pipeline: RAGPipeline | None = None


def get_rag_pipeline() -> RAGPipeline:
    global _rag_pipeline
    if _rag_pipeline is None:
        _rag_pipeline = RAGPipeline()
    return _rag_pipeline
