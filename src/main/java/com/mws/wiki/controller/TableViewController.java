package com.mws.wiki.controller;

import com.mws.wiki.integration.TablesApiClient;
import com.mws.wiki.model.dto.TableRowsResponseDto;
import com.mws.wiki.model.dto.TableViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableViewController {

    private final TablesApiClient tablesApiClient;

    @GetMapping("/{tableId}/views")
    public Mono<ResponseEntity<List<TableViewDto>>> getViews(
            @PathVariable String tableId,
            @RequestHeader("Authorization") String authToken) {
        String token = authToken.replace("Bearer ", "");
        return tablesApiClient.getTableViews(tableId, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{tableId}/rows")
    public Mono<ResponseEntity<TableRowsResponseDto>> getRows(
            @PathVariable String tableId,
            @RequestParam(required = false) String viewId,
            @RequestHeader("Authorization") String authToken) {
        String token = authToken.replace("Bearer ", "");
        return tablesApiClient.getTableRows(tableId, viewId, token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
}

    @GetMapping("/{tableId}/embed")
    public Mono<ResponseEntity<TableRowsResponseDto>> getEmbedPreview(
            @PathVariable String tableId,
            @RequestParam(required = false) String viewId,
            @RequestHeader("Authorization") String authToken) {
        return getRows(tableId, viewId, authToken);
    }
}