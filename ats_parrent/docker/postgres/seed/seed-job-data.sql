-- Seed data for job-service.
--
-- Run this file AFTER job-service has started once. The service uses
-- Hibernate ddl-auto=update, so tables "departments" and "jobs" must already
-- exist before this script is executed.
--
-- Example from the repository root:
--   docker compose exec -T postgres psql -U ats -d job_sdb \
--     < docker/postgres/seed/seed-job-data.sql

\set ON_ERROR_STOP on

BEGIN;

-- A department is required because jobs.department_id is NOT NULL.
INSERT INTO departments (id, department_name, description, manager_id)
VALUES (
    '11111111-1111-1111-1111-111111111111'::uuid,
    'Engineering',
    'Software engineering and platform development',
    NULL
)
ON CONFLICT (id) DO NOTHING;

-- This is the UUID used when testing:
-- /api/v1/jobs/2dd920c1-b29e-4821-82bc-66bb7fa80765
INSERT INTO jobs (
    id,
    department_id,
    recruiter_id,
    title,
    description,
    location,
    salary_min,
    salary_max,
    status,
    utm_source,
    utm_medium,
    deadline,
    published_at
)
VALUES (
    '2dd920c1-b29e-4821-82bc-66bb7fa80765'::uuid,
    '11111111-1111-1111-1111-111111111111'::uuid,
    NULL,
    'Backend Java Developer',
    'Develop and maintain Spring Boot microservices for the TalentHub platform.',
    'Ho Chi Minh City / Hybrid',
    1800.00,
    3000.00,
    'PUBLISHED',
    'seed',
    'local',
    '2027-12-31T23:59:59+07:00'::timestamptz,
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    department_id = EXCLUDED.department_id,
    recruiter_id = EXCLUDED.recruiter_id,
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    location = EXCLUDED.location,
    salary_min = EXCLUDED.salary_min,
    salary_max = EXCLUDED.salary_max,
    status = EXCLUDED.status,
    utm_source = EXCLUDED.utm_source,
    utm_medium = EXCLUDED.utm_medium,
    deadline = EXCLUDED.deadline,
    published_at = EXCLUDED.published_at;

-- Additional records for list/filter testing.
INSERT INTO jobs (
    id,
    department_id,
    recruiter_id,
    title,
    description,
    location,
    salary_min,
    salary_max,
    status,
    utm_source,
    utm_medium,
    deadline,
    published_at
)
VALUES
(
    '2dd920c1-b29e-4821-82bc-66bb7fa80766'::uuid,
    '11111111-1111-1111-1111-111111111111'::uuid,
    NULL,
    'Frontend React Developer',
    'Build responsive user interfaces for recruitment workflows.',
    'Ho Chi Minh City',
    1400.00,
    2400.00,
    'PUBLISHED',
    'seed',
    'local',
    '2027-12-31T23:59:59+07:00'::timestamptz,
    NOW()
),
(
    '2dd920c1-b29e-4821-82bc-66bb7fa80767'::uuid,
    '11111111-1111-1111-1111-111111111111'::uuid,
    NULL,
    'QA Automation Engineer',
    'Design automated tests and improve service quality across the platform.',
    'Remote',
    1200.00,
    2200.00,
    'DRAFT',
    'seed',
    'local',
    '2027-12-31T23:59:59+07:00'::timestamptz,
    NULL
)
ON CONFLICT (id) DO UPDATE SET
    department_id = EXCLUDED.department_id,
    recruiter_id = EXCLUDED.recruiter_id,
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    location = EXCLUDED.location,
    salary_min = EXCLUDED.salary_min,
    salary_max = EXCLUDED.salary_max,
    status = EXCLUDED.status,
    utm_source = EXCLUDED.utm_source,
    utm_medium = EXCLUDED.utm_medium,
    deadline = EXCLUDED.deadline,
    published_at = EXCLUDED.published_at;

COMMIT;

-- Verify the inserted records.
SELECT id, title, status, department_id
FROM jobs
WHERE id IN (
    '2dd920c1-b29e-4821-82bc-66bb7fa80765'::uuid,
    '2dd920c1-b29e-4821-82bc-66bb7fa80766'::uuid,
    '2dd920c1-b29e-4821-82bc-66bb7fa80767'::uuid
)
ORDER BY id;
