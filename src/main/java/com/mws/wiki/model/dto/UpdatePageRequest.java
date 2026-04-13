package com.mws.wiki.model.dto;

import lombok.Data;

@Data
public class UpdatePageRequest {
    private String contentJson;
    private String contentText;
}