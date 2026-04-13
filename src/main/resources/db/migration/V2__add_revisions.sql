CREATE TABLE IF NOT EXISTS page_revision (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID NOT NULL REFERENCES wiki_page(id) ON DELETE CASCADE,
    version INT NOT NULL,
    snapshot_json TEXT NOT NULL,
    author_id VARCHAR(64),
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(page_id, version)
);

ALTER TABLE wiki_page ADD COLUMN IF NOT EXISTS published_version_id UUID REFERENCES page_revision(id);