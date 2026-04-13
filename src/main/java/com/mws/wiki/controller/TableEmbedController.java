package com.mws.wiki.controller;

import com.mws.wiki.integration.TablesApiClient;
import com.mws.wiki.model.dto.TableMetadata;
import com.mws.wiki.model.dto.TableSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableEmbedController {

    private final TablesApiClient tablesApiClient;

    @GetMapping("/search")
    public Mono<ResponseEntity<TableSearchResponse>> searchTables(
            @RequestParam String spaceId,
            @RequestParam(required = false) String q,
            @RequestHeader("Authorization") String authToken) {
        // Убираем "Bearer " если есть, чтобы передать чистый токен
        String token = authToken.replace("Bearer ", "");
        return tablesApiClient.searchTables(spaceId, q, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{tableId}")
    public Mono<ResponseEntity<TableMetadata>> getTable(
            @PathVariable String tableId,
            @RequestHeader("Authorization") String authToken) {
        String token = authToken.replace("Bearer ", "");
        return tablesApiClient.getTableMetadata(tableId, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}