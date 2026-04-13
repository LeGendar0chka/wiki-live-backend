package com.mws.wiki.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class TableSearchResponse {
    private List<TableMetadata> tables;
    private int total;
}