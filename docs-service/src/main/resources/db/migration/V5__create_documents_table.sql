CREATE TABLE IF NOT EXISTS documents
(
    id           uuid PRIMARY KEY,
    user_id      uuid        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name         text        NOT NULL,
    content_type text        NOT NULL,
    size_bytes   bigint      NOT NULL,
    status       text        NOT NULL,
    updated_at   timestamptz NOT NULL
);

CREATE INDEX IF NOT EXISTS documents_user_id_idx ON documents (user_id);
