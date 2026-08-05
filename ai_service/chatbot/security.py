from __future__ import annotations

import logging
import re
from typing import Tuple

logger = logging.getLogger("chatbot.security")


class ChatSecurity:
    """
    Security guardrails for the AI Chatbot.
    Protects against prompt injection, empty/oversized requests, sensitive data leakage, and unsafe outputs.
    """

    PROMPT_INJECTION_PATTERNS = [
        r"ignore\s+(all\s+)?(previous|above)\s+instructions",
        r"system\s+prompt",
        r"override\s+safety",
        r"you\s+are\s+now\s+dan",
        r"jailbreak",
        r"reveal\s+(your|the)\s+(system\s+)?instructions",
        r"pretend\s+you\s+are\s+not\s+an\s+ai",
        r"forget\s+everything\s+you\s+know",
        r"mode\s+developer",
    ]

    SENSITIVE_PII_PATTERNS = [
        r"\b\d{4}[-\s]?\d{4}[-\s]?\d{4}\b",  # Aadhaar card pattern
        r"\b[A-Z]{5}\d{4}[A-Z]{1}\b",        # PAN card pattern
        r"\b\d{16}\b",                       # 16-digit credit card pattern
    ]

    @classmethod
    def validate_message(cls, message: str, max_length: int = 2000) -> Tuple[bool, str]:
        """
        Validates user message.
        Returns (is_valid, error_reason).
        """
        if not message or not message.strip():
            return False, "Message cannot be empty."

        if len(message) > max_length:
            return False, f"Message exceeds maximum allowed length of {max_length} characters."

        # Check Prompt Injection
        msg_lower = message.lower()
        for pattern in cls.PROMPT_INJECTION_PATTERNS:
            if re.search(pattern, msg_lower):
                logger.warning(f"Security Alert: Prompt injection pattern detected: '{pattern}'")
                return False, "Your message contains forbidden instruction patterns. Please ask a valid career query."

        return True, ""

    @classmethod
    def sanitize_output(cls, response_text: str) -> str:
        """
        Sanitizes model output to prevent accidental PII or prompt leakage.
        """
        sanitized = response_text
        for pattern in cls.SENSITIVE_PII_PATTERNS:
            sanitized = re.sub(pattern, "[REDACTED PII]", sanitized)
        return sanitized
