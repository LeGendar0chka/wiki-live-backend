CREATE TABLE IF NOT EXISTS wiki_page (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    space_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    content_json TEXT NOT NULL,
    content_text TEXT,
    status VARCHAR(20) DEFAULT 'draft',
    created_by VARCHAR(64),
    updated_by VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    published_version_id UUID
);

CREATE TABLE IF NOT EXISTS page_link (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    source_page_id UUID NOT NULL REFERENCES wiki_page(id) ON DELETE CASCADE,
    target_page_id UUID NOT NULL REFERENCES wiki_page(id) ON DELETE CASCADE,
    anchor_text VARCHAR(255),
    block_id VARCHAR(64),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS table_embed (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID NOT NULL REFERENCES wiki_page(id) ON DELETE CASCADE,
    block_id VARCHAR(64) NOT NULL,
    table_id VARCHAR(64) NOT NULL,
    view_id VARCHAR(64),
    title_snapshot VARCHAR(255),
    sync_mode VARCHAR(20) DEFAULT 'live',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS comment_thread (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    page_id UUID NOT NULL REFERENCES wiki_page(id) ON DELETE CASCADE,
    block_id VARCHAR(64),
    selection TEXT,
    status VARCHAR(20) DEFAULT 'open',
    created_by VARCHAR(64),
    created_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP
);

CREATE INDEX idx_wiki_page_space_id ON wiki_page(space_id);
CREATE INDEX idx_wiki_page_slug ON wiki_page(slug);
CREATE INDEX idx_page_link_target ON page_link(target_page_id);
CREATE INDEX idx_table_embed_page ON table_embed(page_id);
CREATE INDEX idx_comment_thread_page ON comment_thread(page_id);