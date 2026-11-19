-- Auto-create databases on first container startup
-- This script runs ONCE when PostgreSQL container is created for the first time

CREATE DATABASE spring_dev;

-- Grant all privileges (though postgres user already has them)
GRANT ALL PRIVILEGES ON DATABASE spring_dev TO postgres;
