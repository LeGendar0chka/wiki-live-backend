package com.mws.wiki.repository;

import com.mws.wiki.model.entity.PageLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PageLinkRepository extends JpaRepository<PageLink, UUID> {
    List<PageLink> findByTargetPageId(UUID targetPageId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PageLink p WHERE p.sourcePageId = :sourcePageId")
    void deleteBySourcePageId(UUID sourcePageId);
}