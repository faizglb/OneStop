from __future__ import annotations

import logging
from dataclasses import dataclass, field
from typing import List, Dict, Optional, Any

logger = logging.getLogger("chatbot.vector_store")

# Collections in ChromaDB
COLLECTION_CAREER = "career_docs"
COLLECTION_COLLEGE = "college_docs"
COLLECTION_SCHOLARSHIP = "scholarship_docs"
COLLECTION_EXAM = "exam_docs"
COLLECTION_SKILL = "skill_docs"
COLLECTION_RESUME = "resume_docs"
COLLECTION_GENERAL = "general_docs"

ALL_COLLECTIONS = [
    COLLECTION_CAREER,
    COLLECTION_COLLEGE,
    COLLECTION_SCHOLARSHIP,
    COLLECTION_EXAM,
    COLLECTION_SKILL,
    COLLECTION_RESUME,
    COLLECTION_GENERAL,
]


@dataclass
class RetrievedChunk:
    """A single retrieved text chunk from the vector store."""
    chunk_id: str
    text: str
    source: str
    collection: str
    relevance_score: float
    metadata: Dict[str, Any] = field(default_factory=dict)

    def to_context_string(self) -> str:
        return f"[Source: {self.source}]\n{self.text}"


class VectorStore:
    """
    ChromaDB wrapper for the ACEA chatbot RAG knowledge base.
    Supports local persistent mode and optional HTTP client mode.
    """

    def __init__(self, persist_dir: str = "./vector_db", host: str = "", port: int = 8001):
        self.persist_dir = persist_dir
        self.host = host
        self.port = port
        self._client = None

    def _get_client(self):
        if self._client is None:
            try:
                import chromadb
                if self.host:
                    # Remote ChromaDB server
                    self._client = chromadb.HttpClient(host=self.host, port=self.port)
                    logger.info(f"Connected to ChromaDB server at {self.host}:{self.port}")
                else:
                    # Local persistent ChromaDB
                    self._client = chromadb.PersistentClient(path=self.persist_dir)
                    logger.info(f"Initialised local ChromaDB at: {self.persist_dir}")
            except ImportError:
                logger.error("chromadb not installed. Run: pip install chromadb")
                raise
        return self._client

    def get_or_create_collection(self, collection_name: str):
        """Get or create a ChromaDB collection."""
        client = self._get_client()
        return client.get_or_create_collection(
            name=collection_name,
            metadata={"hnsw:space": "cosine"}
        )

    def ingest_documents(
        self,
        collection_name: str,
        documents: List[str],
        ids: List[str],
        embeddings: List[List[float]],
        metadatas: Optional[List[Dict]] = None,
    ) -> None:
        """Upsert documents with their embeddings into a collection."""
        collection = self.get_or_create_collection(collection_name)
        if metadatas is None:
            metadatas = [{}] * len(documents)

        collection.upsert(
            ids=ids,
            documents=documents,
            embeddings=embeddings,
            metadatas=metadatas,
        )
        logger.info(f"Upserted {len(documents)} chunks into collection '{collection_name}'.")

    def similarity_search(
        self,
        collection_name: str,
        query_embedding: List[float],
        k: int = 5,
        score_threshold: float = 0.0,
    ) -> List[RetrievedChunk]:
        """
        Search a single collection by embedding similarity.
        Returns top-k chunks above the score threshold.
        ChromaDB cosine distance → relevance_score = 1 - distance.
        """
        try:
            collection = self.get_or_create_collection(collection_name)
            count = collection.count()
            if count == 0:
                logger.warning(f"Collection '{collection_name}' is empty — skipping search.")
                return []

            actual_k = min(k, count)
            results = collection.query(
                query_embeddings=[query_embedding],
                n_results=actual_k,
                include=["documents", "distances", "metadatas"],
            )

            chunks: List[RetrievedChunk] = []
            docs = results.get("documents", [[]])[0]
            distances = results.get("distances", [[]])[0]
            metadatas = results.get("metadatas", [[]])[0]
            ids = results.get("ids", [[]])[0]

            for i, (doc, dist, meta, doc_id) in enumerate(zip(docs, distances, metadatas, ids)):
                relevance_score = float(1.0 - dist)  # Convert cosine distance → similarity
                if relevance_score >= score_threshold:
                    chunks.append(RetrievedChunk(
                        chunk_id=doc_id,
                        text=doc,
                        source=meta.get("source", "Knowledge Base"),
                        collection=collection_name,
                        relevance_score=round(relevance_score, 4),
                        metadata=meta,
                    ))

            return sorted(chunks, key=lambda x: x.relevance_score, reverse=True)

        except Exception as e:
            logger.error(f"Similarity search failed on collection '{collection_name}': {e}")
            return []

    def multi_collection_search(
        self,
        query_embedding: List[float],
        collections: List[str],
        k_per_collection: int = 3,
        score_threshold: float = 0.2,
    ) -> List[RetrievedChunk]:
        """
        Search across multiple collections, merge results, deduplicate, and return top-k overall.
        """
        all_chunks: List[RetrievedChunk] = []

        for collection_name in collections:
            chunks = self.similarity_search(
                collection_name=collection_name,
                query_embedding=query_embedding,
                k=k_per_collection,
                score_threshold=score_threshold,
            )
            all_chunks.extend(chunks)

        # Deduplicate by text similarity (exact match)
        seen_texts = set()
        unique_chunks = []
        for chunk in all_chunks:
            key = chunk.text[:100]  # Use first 100 chars as dedup key
            if key not in seen_texts:
                seen_texts.add(key)
                unique_chunks.append(chunk)

        # Return sorted by relevance
        return sorted(unique_chunks, key=lambda x: x.relevance_score, reverse=True)

    def collection_count(self, collection_name: str) -> int:
        """Return the number of documents in a collection."""
        try:
            collection = self.get_or_create_collection(collection_name)
            return collection.count()
        except Exception:
            return 0

    def delete_collection(self, collection_name: str) -> None:
        """Delete a collection (used for re-ingestion)."""
        client = self._get_client()
        try:
            client.delete_collection(collection_name)
            logger.info(f"Deleted collection '{collection_name}'.")
        except Exception as e:
            logger.warning(f"Could not delete collection '{collection_name}': {e}")


# Module-level singleton
_vector_store: VectorStore | None = None


def get_vector_store(persist_dir: str = "./vector_db", host: str = "", port: int = 8001) -> VectorStore:
    global _vector_store
    if _vector_store is None:
        _vector_store = VectorStore(persist_dir=persist_dir, host=host, port=port)
    return _vector_store
