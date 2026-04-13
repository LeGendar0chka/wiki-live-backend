package com.mws.wiki.repository;

import com.mws.wiki.model.entity.TableEmbed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableEmbedRepository extends JpaRepository<TableEmbed, UUID> {
    List<TableEmbed> findByPageId(UUID pageId);
}