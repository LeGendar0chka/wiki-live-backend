package com.mws.wiki.model.dto;

import lombok.Data;

@Data
public class NodeDto {
    private String id;
    private String name;
    private String type;        // "Folder", "Datasheet"
    private String icon;
    private Boolean isFav;
    private Integer permission;
}