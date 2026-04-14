package com.mws.wiki.model.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class TableMetadata {
    private String id;
    private String name;
    private Instant updatedAt;
}