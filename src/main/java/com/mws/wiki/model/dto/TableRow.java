package com.mws.wiki.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TableRow {
    private String recordId;
    private Map<String, Object> fields;
    private Long createdAt;
    private Long updatedAt;
}