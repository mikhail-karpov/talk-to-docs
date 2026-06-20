CREATE TABLE IF NOT EXISTS users
(
    id         uuid PRIMARY KEY,
    email      text UNIQUE NOT NULL,
    password   text        NOT NULL,
    first_name text        NOT NULL,
    last_name  text        NOT NULL,
    created_at timestamp   NOT NULL
);
