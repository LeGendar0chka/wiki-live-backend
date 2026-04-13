package com.mws.wiki.repository;

import com.mws.wiki.model.entity.WikiPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WikiPageRepository extends JpaRepository<WikiPage, UUID> {
    Optional<WikiPage> findBySlug(String slug);
}