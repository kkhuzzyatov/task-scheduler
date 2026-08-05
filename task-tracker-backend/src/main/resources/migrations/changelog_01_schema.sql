CREATE TABLE users
(
    user_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL
);


CREATE TABLE tasks
(
    task_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    title         VARCHAR(255) NOT NULL,

    description   TEXT,

    user_id       UUID NOT NULL
        REFERENCES users (user_id)
            ON DELETE CASCADE,

    completed_at  TIMESTAMP,

    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    deleted_at    TIMESTAMP
);


CREATE INDEX idx_tasks_user_id
    ON tasks (user_id);


CREATE INDEX idx_tasks_user_completed
    ON tasks (user_id, completed_at);


CREATE INDEX idx_tasks_completed_at
    ON tasks (completed_at);


CREATE INDEX idx_tasks_deleted_at
    ON tasks (deleted_at);