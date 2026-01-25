-- Migration script for adding visitor tracking feature
-- Run this in your Supabase SQL Editor to add the site_stats table

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

-- Verify the table was created
SELECT * FROM site_stats;
