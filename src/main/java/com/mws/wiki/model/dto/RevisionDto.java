package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class RevisionDto {
    private UUID id;
    private int version;
    private String snapshotJson;
    private String authorId;
    private Instant createdAt;
}