package com.mws.wiki.integration;

import com.mws.wiki.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TablesApiClient {

    private final WebClient webClient;

    public TablesApiClient(WebClient.Builder builder, @Value("${mws.tables.api.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    private WebClient getClient(String authToken) {
        return webClient.mutate()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();
    }

    // ==================== Поиск/список таблиц ====================
    // GET /fusion/v1/spaces/{spaceId}/nodes?type=2  (2 = Datasheet)
    public Mono<List<TableMetadata>> searchDatasheets(String spaceId, String query, String authToken) {
        return getClient(authToken).get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fusion/v1/spaces/{spaceId}/nodes")
                        .queryParam("type", 2)   // фильтр только таблиц
                        .build(spaceId))
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Fusion API error (nodes): {} {}", response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Fusion API error: " + response.statusCode()));
                                }))
                .bodyToMono(new ParameterizedTypeReference<FusionResponse<NodesData>>() {})
                .map(response -> {
                    if (response.isSuccess() && response.getData() != null) {
                        List<NodeDto> nodes = response.getData().getNodes();
                        if (nodes != null) {
                            return nodes.stream()
                                    .filter(node -> query == null || query.isEmpty() ||
                                            node.getName().toLowerCase().contains(query.toLowerCase()))
                                    .map(this::nodeToTableMetadata)
                                    .collect(Collectors.toList());
                        }
                    }
                    return Collections.<TableMetadata>emptyList();
                })
                .onErrorReturn(Collections.emptyList());
    }

    // ==================== Метаданные таблицы ====================
    // GET /fusion/v1/nodes/{dstId}
    public Mono<TableMetadata> getDatasheetMetadata(String dstId, String authToken) {
        return getClient(authToken).get()
                .uri("/fusion/v1/nodes/{nodeId}", dstId)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Fusion API error (node): {} {}", response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Fusion API error: " + response.statusCode()));
                                }))
                .bodyToMono(new ParameterizedTypeReference<FusionResponse<NodeDto>>() {})
                .map(response -> response.isSuccess() && response.getData() != null ?
                        nodeToTableMetadata(response.getData()) : null)
                .onErrorReturn(null);
    }

    // ==================== Views ====================
    // GET /fusion/v1/datasheets/{dstId}/views
    public Mono<List<TableViewDto>> getViews(String dstId, String authToken) {
        return getClient(authToken).get()
                .uri("/fusion/v1/datasheets/{dstId}/views", dstId)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Fusion API error (views): {} {}", response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Fusion API error: " + response.statusCode()));
                                }))
                .bodyToMono(new ParameterizedTypeReference<FusionResponse<FusionViewsData>>() {})
                .map(response -> response.isSuccess() && response.getData() != null ?
                        response.getData().getViews() : Collections.emptyList())
                .onErrorReturn(Collections.emptyList());
    }

    // ==================== Fields (колонки) ====================
    // GET /fusion/v1/datasheets/{dstId}/fields?viewId=...
    public Mono<List<TableColumnDto>> getFields(String dstId, String viewId, String authToken) {
        return getClient(authToken).get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/fusion/v1/datasheets/{dstId}/fields");
                    if (viewId != null) uriBuilder.queryParam("viewId", viewId);
                    return uriBuilder.build(dstId);
                })
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Fusion API error (fields): {} {}", response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Fusion API error: " + response.statusCode()));
                                }))
                .bodyToMono(new ParameterizedTypeReference<FusionResponse<FusionFieldsData>>() {})
                .map(response -> response.isSuccess() && response.getData() != null ?
                        response.getData().getFields() : Collections.emptyList())
                .onErrorReturn(Collections.emptyList());
    }

    // ==================== Rows (записи) ====================
    // GET /fusion/v1/datasheets/{dstId}/records?viewId=...
    public Mono<TableRowsResponseDto> getRows(String dstId, String viewId, String authToken) {
        Mono<List<TableRow>> rowsMono = getClient(authToken).get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/fusion/v1/datasheets/{dstId}/records");
                    if (viewId != null) uriBuilder.queryParam("viewId", viewId);
                    return uriBuilder.build(dstId);
                })
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Fusion API error (records): {} {}", response.statusCode(), body);
                                    return Mono.error(new RuntimeException("Fusion API error: " + response.statusCode()));
                                }))
                .bodyToMono(new ParameterizedTypeReference<FusionResponse<FusionRecordsData>>() {})
                .map(response -> response.isSuccess() && response.getData() != null ?
                        response.getData().getRecords() : Collections.emptyList())
                .onErrorReturn(Collections.emptyList());

        Mono<List<TableColumnDto>> fieldsMono = getFields(dstId, viewId, authToken);
        Mono<TableMetadata> metaMono = getDatasheetMetadata(dstId, authToken);
        Mono<List<TableViewDto>> viewsMono = getViews(dstId, authToken);

        return Mono.zip(rowsMono, fieldsMono, metaMono, viewsMono)
                .map(tuple -> {
                    List<TableRow> rows = tuple.getT1();
                    List<TableColumnDto> columns = tuple.getT2();
                    TableMetadata meta = tuple.getT3();
                    List<TableViewDto> views = tuple.getT4();

                    String viewName = views.stream()
                            .filter(v -> v.getId().equals(viewId))
                            .findFirst()
                            .map(TableViewDto::getName)
                            .orElse("Default");

                    return TableRowsResponseDto.builder()
                            .tableId(dstId)
                            .viewId(viewId)
                            .viewName(viewName)
                            .columns(columns)
                            .rows(rows)
                            .updatedAt(meta != null ? meta.getUpdatedAt() : null)
                            .accessDenied(false)
                            .deleted(false)
                            .empty(rows.isEmpty())
                            .build();
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Upstream error while building table preview: {}", ex.getMessage());
                    TableRowsResponseDto errorResponse = TableRowsResponseDto.builder()
                            .tableId(dstId)
                            .viewId(viewId)
                            .accessDenied(ex.getStatusCode() == HttpStatus.UNAUTHORIZED || ex.getStatusCode() == HttpStatus.FORBIDDEN)
                            .deleted(ex.getStatusCode() == HttpStatus.NOT_FOUND)
                            .empty(true)
                            .build();
                    return Mono.just(errorResponse);
                });
    }

    private TableMetadata nodeToTableMetadata(NodeDto node) {
        TableMetadata meta = new TableMetadata();
        meta.setId(node.getId());
        meta.setName(node.getName());
        // Поле updatedAt может отсутствовать в NodeDto
        return meta;
    }
}