package com.mws.wiki.repository;

import com.mws.wiki.model.entity.PageRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PageRevisionRepository extends JpaRepository<PageRevision, UUID> {
    List<PageRevision> findByPageIdOrderByVersionDesc(UUID pageId);
}