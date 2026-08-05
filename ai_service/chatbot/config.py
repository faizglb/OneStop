import os
from dataclasses import dataclass


@dataclass(frozen=True)
class ChatbotConfig:
    """
    Centralised configuration for the AI Career Chatbot.
    All values are read from environment variables with safe production-grade defaults.
    """

    # ── LLM ────────────────────────────────────────────────────────────────
    gemini_api_key: str = os.getenv("GEMINI_API_KEY", "")
    gemini_model: str = os.getenv("GEMINI_MODEL", "gemini-1.5-flash")
    temperature: float = float(os.getenv("CHATBOT_TEMPERATURE", "0.3"))
    max_output_tokens: int = int(os.getenv("CHATBOT_MAX_TOKENS", "2048"))
    top_p: float = float(os.getenv("CHATBOT_TOP_P", "0.9"))

    # ── Embeddings ──────────────────────────────────────────────────────────
    embedding_model: str = os.getenv(
        "EMBEDDING_MODEL", "sentence-transformers/all-MiniLM-L6-v2"
    )
    embedding_dimension: int = 384  # fixed for all-MiniLM-L6-v2

    # ── ChromaDB ────────────────────────────────────────────────────────────
    chroma_persist_dir: str = os.getenv(
        "CHROMA_PERSIST_DIR", "./vector_db"
    )
    chroma_host: str = os.getenv("CHROMA_HOST", "")  # empty = local disk mode
    chroma_port: int = int(os.getenv("CHROMA_PORT", "8001"))

    # ── RAG Retrieval ───────────────────────────────────────────────────────
    retrieval_top_k: int = int(os.getenv("RETRIEVAL_TOP_K", "5"))
    retrieval_score_threshold: float = float(
        os.getenv("RETRIEVAL_SCORE_THRESHOLD", "0.3")
    )
    chunk_size: int = int(os.getenv("CHUNK_SIZE", "512"))
    chunk_overlap: int = int(os.getenv("CHUNK_OVERLAP", "50"))

    # ── Memory ──────────────────────────────────────────────────────────────
    memory_window_size: int = int(os.getenv("MEMORY_WINDOW_SIZE", "10"))
    memory_summarize_after: int = int(os.getenv("MEMORY_SUMMARIZE_AFTER", "20"))

    # ── Security ────────────────────────────────────────────────────────────
    max_message_length: int = int(os.getenv("MAX_MESSAGE_LENGTH", "2000"))
    min_confidence_score: float = float(os.getenv("MIN_CONFIDENCE_SCORE", "0.1"))

    # ── Service ─────────────────────────────────────────────────────────────
    host: str = os.getenv("HOST", "0.0.0.0")
    port: int = int(os.getenv("PORT", "8000"))


# Singleton instance
chatbot_config = ChatbotConfig()
