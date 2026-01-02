-- Seed data for development
-- This file is loaded automatically by Spring Boot on startup

-- Create an admin user (password: admin123 - BCrypt encoded)
-- Using ON CONFLICT to handle existing user
INSERT INTO users (username, email, password, display_name, role, enabled, created_at, updated_at)
VALUES ('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n0MgZj7LfmOZCU6Q3tC3K', 'Blog Admin', 'ADMIN', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

