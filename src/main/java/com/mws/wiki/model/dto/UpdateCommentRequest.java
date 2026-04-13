package com.mws.wiki.model.dto;

import lombok.Data;

@Data
public class UpdateCommentRequest {
    private String status; // "open" или "resolved"
}