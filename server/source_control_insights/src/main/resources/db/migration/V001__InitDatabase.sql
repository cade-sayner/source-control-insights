CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_id UUID UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE repositories (
    repo_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repo_name VARCHAR(255) NOT NULL,
    repo_url VARCHAR(255) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE commits (
    commit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contributor_id UUID NOT NULL REFERENCES users(user_id),
    commit_hash VARCHAR(50) UNIQUE NOT NULL,
    message TEXT NOT NULL,
    commit_timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    repo_id UUID REFERENCES repositories(repo_id) ON DELETE CASCADE
);

CREATE TABLE commit_files (
    file_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    commit_id UUID NOT NULL REFERENCES commits(commit_id) ON DELETE CASCADE,
    file_path TEXT NOT NULL,
    change_type VARCHAR(50) NOT NULL
);

CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(255) UNIQUE NOT NULL,
    role_description VARCHAR(255)
);

CREATE TABLE user_roles (
    user_role_id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE timesheets (
    timesheet_id SERIAL PRIMARY KEY,
    duration INTEGER NOT NULL,
    contributor_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    repo_id UUID NOT NULL REFERENCES repositories(repo_id) ON DELETE CASCADE
);
