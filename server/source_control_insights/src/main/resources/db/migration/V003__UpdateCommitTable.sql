ALTER TABLE commits
ADD COLUMN files_changed INTEGER DEFAULT 0,
ADD COLUMN insertions INTEGER DEFAULT 0,
ADD COLUMN deletions INTEGER DEFAULT 0;
