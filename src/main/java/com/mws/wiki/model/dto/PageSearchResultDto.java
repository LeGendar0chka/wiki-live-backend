package com.mws.wiki.model.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PageSearchResultDto {
    private UUID id;
    private String title;
    private String slug;
    private Instant updatedAt;
}