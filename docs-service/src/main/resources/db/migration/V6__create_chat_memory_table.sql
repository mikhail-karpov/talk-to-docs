CREATE TABLE IF NOT EXISTS spring_ai_chat_memory
(
    conversation_id text        NOT NULL,
    content         text        NOT NULL,
    type            text        NOT NULL,
    "timestamp"     timestamptz NOT NULL,
    CONSTRAINT type_check CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
);

CREATE INDEX IF NOT EXISTS spring_ai_chat_memory_conversation_id_timestamp_idx
    ON spring_ai_chat_memory (conversation_id, "timestamp");
