package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CommentDto {
    private UUID id;
    private UUID pageId;
    private String blockId;
    private String selection;
    private String content;
    private String status;
    private String createdBy;
    private Instant createdAt;
    private Instant resolvedAt;
    private UUID parentId;  // null для корневого треда
}