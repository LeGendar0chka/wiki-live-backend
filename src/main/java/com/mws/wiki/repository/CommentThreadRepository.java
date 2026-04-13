package com.mws.wiki.repository;

import com.mws.wiki.model.entity.CommentThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentThreadRepository extends JpaRepository<CommentThread, UUID> {
    List<CommentThread> findByPageId(UUID pageId);
}