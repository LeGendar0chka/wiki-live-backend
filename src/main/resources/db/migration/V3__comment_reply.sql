ALTER TABLE comment_thread ADD COLUMN IF NOT EXISTS parent_id UUID REFERENCES comment_thread(id);
ALTER TABLE comment_thread ADD COLUMN IF NOT EXISTS content TEXT;