package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GptRequest {
    private String prompt;
    private String context;
    private int maxTokens;
}