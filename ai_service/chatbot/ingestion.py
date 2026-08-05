from __future__ import annotations

import json
import logging
import os
import re
from pathlib import Path
from typing import List, Dict, Any, Tuple

from chatbot.embeddings import get_embedding_service
from chatbot.vector_store import (
    get_vector_store,
    COLLECTION_CAREER, COLLECTION_COLLEGE, COLLECTION_SCHOLARSHIP,
    COLLECTION_EXAM, COLLECTION_SKILL, COLLECTION_RESUME, COLLECTION_GENERAL,
)

logger = logging.getLogger("chatbot.ingestion")

# Directory containing JSON knowledge files
KB_DIR = Path(__file__).parent / "knowledge_base"

# Mapping: JSON file → ChromaDB collection
FILE_TO_COLLECTION: Dict[str, str] = {
    "careers.json": COLLECTION_CAREER,
    "colleges.json": COLLECTION_COLLEGE,
    "scholarships.json": COLLECTION_SCHOLARSHIP,
    "entrance_exams.json": COLLECTION_EXAM,
    "skills_guide.json": COLLECTION_SKILL,
    "salary_trends.json": COLLECTION_SKILL,  # salary goes into skill collection
    "resume_tips.json": COLLECTION_RESUME,
    "faq.json": COLLECTION_GENERAL,
    "industry_reports.json": COLLECTION_GENERAL,
    "certifications.json": COLLECTION_GENERAL,
    "admission_guidelines.json": COLLECTION_GENERAL,
    "degree_programs.json": COLLECTION_GENERAL,
}


def _flatten_json_to_text(obj: Any, prefix: str = "") -> str:
    """
    Recursively flatten a JSON object into a readable text representation
    for embedding. Lists are joined; dicts become 'key: value' pairs.
    """
    if isinstance(obj, str):
        return obj
    if isinstance(obj, (int, float, bool)):
        return str(obj)
    if isinstance(obj, list):
        return " | ".join(_flatten_json_to_text(item) for item in obj if item)
    if isinstance(obj, dict):
        parts = []
        for k, v in obj.items():
            label = k.replace("_", " ").title()
            value = _flatten_json_to_text(v)
            if value:
                parts.append(f"{label}: {value}")
        return ". ".join(parts)
    return ""


def _chunk_text(text: str, chunk_size: int = 512, overlap: int = 50) -> List[str]:
    """
    Split text into overlapping chunks by word count.
    """
    words = text.split()
    if len(words) <= chunk_size:
        return [text]

    chunks = []
    start = 0
    while start < len(words):
        end = start + chunk_size
        chunk = " ".join(words[start:end])
        chunks.append(chunk)
        start += chunk_size - overlap

    return chunks


def _load_json_file(filepath: Path) -> List[Dict]:
    """Load and parse a JSON file. Returns list of dicts."""
    with open(filepath, "r", encoding="utf-8") as f:
        data = json.load(f)
    if isinstance(data, dict):
        return [data]
    return data


def _document_to_chunks(
    doc: Dict, source_file: str, chunk_size: int = 512, overlap: int = 50
) -> List[Tuple[str, str, str, Dict]]:
    """
    Convert a JSON document to (chunk_id, text, source_name, metadata) tuples.
    """
    doc_id = doc.get("id", "unknown")
    title = doc.get("name") or doc.get("title") or doc.get("question") or doc.get("skill_name") or doc_id
    source_name = f"{source_file} — {title}"

    # Convert entire doc to readable text
    full_text = _flatten_json_to_text(doc)

    # Chunk the text
    chunks = _chunk_text(full_text, chunk_size, overlap)

    result = []
    for i, chunk_text in enumerate(chunks):
        chunk_id = f"{doc_id}_chunk_{i}"
        metadata = {
            "source": source_name,
            "doc_id": doc_id,
            "title": str(title),
            "file": source_file,
            "chunk_index": i,
        }
        result.append((chunk_id, chunk_text, source_name, metadata))

    return result


class KnowledgeBaseIngester:
    """
    Loads knowledge base JSON files, generates embeddings,
    and upserts them into ChromaDB. Idempotent — skips
    already-ingested collections.
    """

    def __init__(self, chunk_size: int = 512, chunk_overlap: int = 50, force_reingest: bool = False):
        self.chunk_size = chunk_size
        self.chunk_overlap = chunk_overlap
        self.force_reingest = force_reingest
        self.embedding_service = get_embedding_service()
        self.vector_store = get_vector_store()

    def _should_skip_collection(self, collection_name: str) -> bool:
        """Skip ingestion if collection already has documents (unless force)."""
        if self.force_reingest:
            return False
        count = self.vector_store.collection_count(collection_name)
        if count > 0:
            logger.info(f"Collection '{collection_name}' already has {count} docs — skipping ingestion.")
            return True
        return False

    def ingest_file(self, filepath: Path, collection_name: str) -> int:
        """Ingest a single JSON file into a ChromaDB collection. Returns chunk count."""
        logger.info(f"Ingesting: {filepath.name} → {collection_name}")

        try:
            documents = _load_json_file(filepath)
        except Exception as e:
            logger.error(f"Failed to load {filepath}: {e}")
            return 0

        all_chunks: List[Tuple[str, str, str, Dict]] = []
        for doc in documents:
            chunks = _document_to_chunks(doc, filepath.name, self.chunk_size, self.chunk_overlap)
            all_chunks.extend(chunks)

        if not all_chunks:
            logger.warning(f"No chunks generated from {filepath.name}")
            return 0

        # Generate embeddings in batch
        texts = [chunk[1] for chunk in all_chunks]
        try:
            embeddings = self.embedding_service.embed_batch(texts)
        except Exception as e:
            logger.error(f"Embedding generation failed for {filepath.name}: {e}")
            return 0

        # Prepare for ChromaDB
        ids = [chunk[0] for chunk in all_chunks]
        metadatas = [chunk[3] for chunk in all_chunks]

        self.vector_store.ingest_documents(
            collection_name=collection_name,
            documents=texts,
            ids=ids,
            embeddings=embeddings,
            metadatas=metadatas,
        )

        logger.info(f"  ✓ {len(all_chunks)} chunks from {filepath.name} ingested into '{collection_name}'.")
        return len(all_chunks)

    def ingest_all(self) -> Dict[str, int]:
        """
        Ingest all knowledge base JSON files.
        Returns dict of {collection_name: chunks_ingested}.
        """
        logger.info("=" * 60)
        logger.info("Starting Knowledge Base Ingestion")
        logger.info("=" * 60)

        if not KB_DIR.exists():
            logger.error(f"Knowledge base directory not found: {KB_DIR}")
            return {}

        # Track total per collection
        collection_totals: Dict[str, int] = {}

        # Check which collections need ingestion
        collections_to_ingest = set()
        for filename, collection in FILE_TO_COLLECTION.items():
            filepath = KB_DIR / filename
            if filepath.exists():
                if not self._should_skip_collection(collection):
                    collections_to_ingest.add(collection)

        if not collections_to_ingest and not self.force_reingest:
            logger.info("All collections already populated. Skipping ingestion. (Set force_reingest=True to override)")
            return {c: self.vector_store.collection_count(c) for c in FILE_TO_COLLECTION.values()}

        # Ingest each file
        for filename, collection in FILE_TO_COLLECTION.items():
            filepath = KB_DIR / filename
            if not filepath.exists():
                logger.warning(f"Knowledge base file not found: {filepath} — skipping.")
                continue
            if collection not in collections_to_ingest:
                continue

            count = self.ingest_file(filepath, collection)
            collection_totals[collection] = collection_totals.get(collection, 0) + count

        logger.info("=" * 60)
        logger.info("Ingestion Complete!")
        for coll, total in collection_totals.items():
            logger.info(f"  {coll}: {total} chunks")
        logger.info("=" * 60)

        return collection_totals


def run_ingestion(force_reingest: bool = False) -> Dict[str, int]:
    """Entry point for knowledge base ingestion. Called at startup."""
    ingester = KnowledgeBaseIngester(force_reingest=force_reingest)
    return ingester.ingest_all()
