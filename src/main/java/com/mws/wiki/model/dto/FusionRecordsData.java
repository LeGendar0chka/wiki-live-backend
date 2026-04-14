package com.mws.wiki.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class FusionRecordsData {
    private List<TableRow> records;
    private Integer total;
    private Integer pageNum;
    private Integer pageSize;
}