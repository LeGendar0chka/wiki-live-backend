package com.mws.wiki.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class FusionFieldsData {
    private List<TableColumnDto> fields;
}
