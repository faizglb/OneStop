-- Flyway Migration V13: Chat Module Schema
-- Creates chat_sessions, chat_messages, and chat_feedback tables in identity_schema

CREATE TABLE IF NOT EXISTS identity_schema.chat_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL DEFAULT 'New Conversation',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_message_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    message_count INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT chk_chat_status CHECK (status IN ('ACTIVE', 'ARCHIVED'))
);

CREATE INDEX idx_chat_sessions_student ON identity_schema.chat_sessions(student_id, last_message_at DESC);

CREATE TABLE IF NOT EXISTS identity_schema.chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES identity_schema.chat_sessions(id) ON DELETE CASCADE,
    sender VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    agent_used VARCHAR(50),
    confidence_score DECIMAL(5,4),
    sources JSONB,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_chat_sender CHECK (sender IN ('USER', 'ASSISTANT'))
);

CREATE INDEX idx_chat_messages_session ON identity_schema.chat_messages(session_id, timestamp ASC);

CREATE TABLE IF NOT EXISTS identity_schema.chat_feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id UUID NOT NULL REFERENCES identity_schema.chat_messages(id) ON DELETE CASCADE,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    feedback_text VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_feedback_message ON identity_schema.chat_feedback(message_id);
