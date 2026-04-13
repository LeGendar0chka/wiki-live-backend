package com.mws.wiki.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "page_link")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "source_page_id", nullable = false)
    private UUID sourcePageId;

    @Column(name = "target_page_id", nullable = false)
    private UUID targetPageId;

    @Column(name = "anchor_text")
    private String anchorText;

    @Column(name = "block_id")
    private String blockId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}