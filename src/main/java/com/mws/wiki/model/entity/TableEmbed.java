package com.mws.wiki.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "table_embed")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableEmbed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "page_id", nullable = false)
    private UUID pageId;

    @Column(name = "block_id", nullable = false)
    private String blockId;

    @Column(name = "table_id", nullable = false)
    private String tableId;

    @Column(name = "view_id")
    private String viewId;

    @Column(name = "title_snapshot")
    private String titleSnapshot;

    @Column(name = "sync_mode")
    private String syncMode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}