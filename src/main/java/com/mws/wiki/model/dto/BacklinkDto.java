package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BacklinkDto {
    private UUID sourcePageId;
    private String sourcePageTitle;
    private String anchorText;
}