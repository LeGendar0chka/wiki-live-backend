package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableViewDto {
    private String id;
    private String name;
}