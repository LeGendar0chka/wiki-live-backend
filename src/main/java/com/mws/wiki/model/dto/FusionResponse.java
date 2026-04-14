package com.mws.wiki.model.dto;

import lombok.Data;

@Data
public class FusionResponse<T> {
    private int code;
    private boolean success;
    private String message;
    private T data;
}