CREATE TABLE IF NOT EXISTS project
(
    id           uuid PRIMARY KEY,
    user_id      uuid        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title        text        NOT NULL,
    description  text,
    updated_at   timestamptz NOT NULL
);

CREATE INDEX IF NOT EXISTS project_user_id_idx ON project (user_id);
