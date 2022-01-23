CREATE TABLE IF NOT EXISTS tracks(
    round_id uuid DEFAULT random_uuid(),
    artist VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    local_path VARCHAR(512) NOT NULL PRIMARY KEY
)