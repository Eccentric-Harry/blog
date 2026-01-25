-- Schema adjustments to keep Hibernate ddl-auto=update from failing on existing data.
-- This runs on startup because spring.sql.init.mode=always.

-- Add the archived column safely for existing rows.
ALTER TABLE IF EXISTS posts
    ADD COLUMN IF NOT EXISTS archived BOOLEAN;

-- Backfill existing rows.
UPDATE posts
SET archived = FALSE
WHERE archived IS NULL;

-- Enforce default and NOT NULL.
ALTER TABLE IF EXISTS posts
    ALTER COLUMN archived SET DEFAULT FALSE;

ALTER TABLE IF EXISTS posts
    ALTER COLUMN archived SET NOT NULL;

-- Optional index used by queries filtering archived.
CREATE INDEX IF NOT EXISTS idx_posts_archived ON posts(archived);

-- Site stats table for visitor counting
CREATE TABLE IF NOT EXISTS site_stats (
    id BIGINT PRIMARY KEY,
    total_visitors BIGINT NOT NULL DEFAULT 0,
    last_updated TIMESTAMP
);

-- Initialize the global stats row if it doesn't exist
INSERT INTO site_stats (id, total_visitors, last_updated)
VALUES (1, 0, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

