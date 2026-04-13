package com.mws.wiki.model.dto;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String blockId;
    private String selection;
    private String content;
}