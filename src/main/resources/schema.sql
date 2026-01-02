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

