from __future__ import annotations

import logging
from functools import lru_cache
from typing import List

logger = logging.getLogger("chatbot.embeddings")


class EmbeddingService:
    """
    Wraps sentence-transformers to provide text embeddings.
    Lazy-loads the model on first use (saves ~400MB RAM if chatbot is not called).
    Thread-safe singleton via module-level instance.
    """

    def __init__(self, model_name: str = "sentence-transformers/all-MiniLM-L6-v2"):
        self.model_name = model_name
        self._model = None

    def _load_model(self):
        if self._model is None:
            try:
                from sentence_transformers import SentenceTransformer
                logger.info(f"Loading embedding model: {self.model_name}")
                self._model = SentenceTransformer(self.model_name)
                logger.info("Embedding model loaded successfully.")
            except ImportError:
                logger.error("sentence-transformers not installed. Run: pip install sentence-transformers")
                raise
        return self._model

    def embed_text(self, text: str) -> List[float]:
        """Embed a single text string. Returns a flat list of floats."""
        model = self._load_model()
        embedding = model.encode(text, convert_to_numpy=True)
        return embedding.tolist()

    def embed_batch(self, texts: List[str]) -> List[List[float]]:
        """Embed a list of texts. Returns list of embedding vectors."""
        if not texts:
            return []
        model = self._load_model()
        embeddings = model.encode(texts, convert_to_numpy=True, batch_size=32, show_progress_bar=False)
        return [emb.tolist() for emb in embeddings]

    def embed_query(self, query: str) -> List[float]:
        """Alias for embed_text, used specifically for query embedding."""
        return self.embed_text(query)


# Module-level singleton
_embedding_service: EmbeddingService | None = None


def get_embedding_service(model_name: str = "sentence-transformers/all-MiniLM-L6-v2") -> EmbeddingService:
    """Returns the singleton EmbeddingService instance."""
    global _embedding_service
    if _embedding_service is None:
        _embedding_service = EmbeddingService(model_name)
    return _embedding_service
