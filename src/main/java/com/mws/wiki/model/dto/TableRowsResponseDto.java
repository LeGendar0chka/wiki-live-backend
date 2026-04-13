package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TableRowsResponseDto {
    private String tableId;
    private String viewId;
    private String viewName;
    private List<TableColumnDto> columns;
    private List<TableRow> rows;
    private Instant updatedAt;
    private Boolean accessDenied;
    private Boolean deleted;
    private Boolean empty;
}