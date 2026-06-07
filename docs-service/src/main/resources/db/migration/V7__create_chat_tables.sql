CREATE TABLE IF NOT EXISTS conversation
(
    id         uuid PRIMARY KEY,
    project_id uuid        NOT NULL REFERENCES project (id) ON DELETE CASCADE,
    user_id    uuid        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title      text        NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE INDEX IF NOT EXISTS conversation_user_id_idx ON conversation (user_id);

CREATE TABLE IF NOT EXISTS chat_messages
(
    id              uuid PRIMARY KEY,
    conversation_id uuid        NOT NULL REFERENCES conversation (id) ON DELETE CASCADE,
    user_id         uuid        NOT NULL,
    author_type     text        NOT NULL,
    content         text        NOT NULL,
    created_at      timestamptz NOT NULL
);

CREATE INDEX IF NOT EXISTS chat_messages_conversation_id_idx ON chat_messages (conversation_id);
