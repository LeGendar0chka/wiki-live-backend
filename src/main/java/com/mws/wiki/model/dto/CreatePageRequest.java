package com.mws.wiki.model.dto;

import lombok.Data;

@Data
public class CreatePageRequest {
    private String spaceId;
    private String title;
    private String contentJson;
}