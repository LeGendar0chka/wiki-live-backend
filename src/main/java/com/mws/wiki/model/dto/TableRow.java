package com.mws.wiki.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TableRow {
    private String id;
    private Map<String, Object> cells;
}