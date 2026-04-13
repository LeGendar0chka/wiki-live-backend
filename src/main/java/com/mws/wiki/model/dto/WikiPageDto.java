package com.mws.wiki.model.dto;

import com.mws.wiki.model.entity.WikiPage;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class WikiPageDto {
    private UUID id;
    private String spaceId;
    private String title;
    private String slug;
    private String contentJson;
    private String status;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public static WikiPageDto fromEntity(WikiPage entity) {
        return WikiPageDto.builder()
                .id(entity.getId())
                .spaceId(entity.getSpaceId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .contentJson(entity.getContentJson())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}