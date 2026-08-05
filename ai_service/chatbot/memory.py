from __future__ import annotations

import logging
import time
from collections import defaultdict, deque
from dataclasses import dataclass, field
from threading import Lock
from typing import Deque, Dict, List, Optional, Set

logger = logging.getLogger("chatbot.memory")


@dataclass
class ChatTurn:
    """A single turn in a conversation."""
    role: str          # "user" or "assistant"
    content: str
    timestamp: float = field(default_factory=time.time)
    agent_used: Optional[str] = None

    def to_dict(self) -> dict:
        return {
            "role": self.role,
            "content": self.content,
            "timestamp": self.timestamp,
            "agent_used": self.agent_used,
        }

    def to_langchain_message(self) -> dict:
        """Convert to LangChain-compatible message dict."""
        return {"role": self.role, "content": self.content}


@dataclass
class SessionMemory:
    """
    In-memory conversation state for a single session.
    Holds full history, a sliding context window, and a topic tracker.
    """
    session_id: str
    student_id: str
    messages: List[ChatTurn] = field(default_factory=list)
    recent_topics: Set[str] = field(default_factory=set)
    summary: str = ""
    created_at: float = field(default_factory=time.time)
    last_active: float = field(default_factory=time.time)
    turn_count: int = 0

    def add_message(self, role: str, content: str, agent_used: Optional[str] = None) -> None:
        turn = ChatTurn(role=role, content=content, agent_used=agent_used)
        self.messages.append(turn)
        self.last_active = time.time()
        self.turn_count += 1

    def get_context_window(self, n: int = 10) -> List[ChatTurn]:
        """Returns the last n turns (both user and assistant)."""
        return self.messages[-n:]

    def get_context_as_text(self, n: int = 10) -> str:
        """Returns the last n turns as a formatted text block."""
        window = self.get_context_window(n)
        lines = []
        for turn in window:
            prefix = "Student" if turn.role == "user" else "AI Advisor"
            lines.append(f"{prefix}: {turn.content}")
        return "\n".join(lines)

    def to_langchain_messages(self, n: int = 10) -> List[dict]:
        """Returns recent turns as list of LangChain-style message dicts."""
        return [t.to_langchain_message() for t in self.get_context_window(n)]

    def add_topic(self, topic: str) -> None:
        self.recent_topics.add(topic.lower().strip())

    def needs_summarization(self, threshold: int = 20) -> bool:
        return len(self.messages) >= threshold and len(self.messages) % threshold == 0

    def is_stale(self, ttl_seconds: int = 3600) -> bool:
        """Session is stale if inactive for TTL seconds."""
        return (time.time() - self.last_active) > ttl_seconds


class ConversationMemory:
    """
    Thread-safe in-memory store for all active chat sessions.
    In production, replace the dict with a Redis backend for horizontal scaling.
    """

    def __init__(self, window_size: int = 10, summarize_after: int = 20, session_ttl_seconds: int = 3600):
        self._sessions: Dict[str, SessionMemory] = {}
        self._lock = Lock()
        self.window_size = window_size
        self.summarize_after = summarize_after
        self.session_ttl_seconds = session_ttl_seconds

    def get_or_create_session(self, session_id: str, student_id: str) -> SessionMemory:
        """Get existing session or create a new one."""
        with self._lock:
            if session_id not in self._sessions:
                self._sessions[session_id] = SessionMemory(
                    session_id=session_id,
                    student_id=student_id,
                )
                logger.info(f"Created new session: {session_id} for student: {student_id}")
            return self._sessions[session_id]

    def get_session(self, session_id: str) -> Optional[SessionMemory]:
        """Get an existing session. Returns None if not found."""
        with self._lock:
            return self._sessions.get(session_id)

    def add_user_message(self, session_id: str, student_id: str, message: str) -> None:
        session = self.get_or_create_session(session_id, student_id)
        session.add_message(role="user", content=message)

    def add_assistant_message(
        self, session_id: str, student_id: str, message: str, agent_used: Optional[str] = None
    ) -> None:
        session = self.get_or_create_session(session_id, student_id)
        session.add_message(role="assistant", content=message, agent_used=agent_used)

    def get_context_window(self, session_id: str, student_id: str) -> List[ChatTurn]:
        session = self.get_or_create_session(session_id, student_id)
        return session.get_context_window(self.window_size)

    def get_context_text(self, session_id: str, student_id: str) -> str:
        session = self.get_or_create_session(session_id, student_id)
        return session.get_context_as_text(self.window_size)

    def delete_session(self, session_id: str) -> bool:
        """Delete a session from memory. Returns True if deleted."""
        with self._lock:
            if session_id in self._sessions:
                del self._sessions[session_id]
                logger.info(f"Deleted session: {session_id}")
                return True
            return False

    def update_session_summary(self, session_id: str, summary: str) -> None:
        with self._lock:
            if session_id in self._sessions:
                self._sessions[session_id].summary = summary

    def cleanup_stale_sessions(self) -> int:
        """Remove sessions that have been inactive beyond TTL. Returns count removed."""
        with self._lock:
            stale = [sid for sid, s in self._sessions.items() if s.is_stale(self.session_ttl_seconds)]
            for sid in stale:
                del self._sessions[sid]
            if stale:
                logger.info(f"Cleaned up {len(stale)} stale sessions.")
            return len(stale)

    def active_session_count(self) -> int:
        return len(self._sessions)


# Module-level singleton
_conversation_memory: ConversationMemory | None = None


def get_conversation_memory(window_size: int = 10, summarize_after: int = 20) -> ConversationMemory:
    global _conversation_memory
    if _conversation_memory is None:
        _conversation_memory = ConversationMemory(
            window_size=window_size,
            summarize_after=summarize_after,
        )
    return _conversation_memory
