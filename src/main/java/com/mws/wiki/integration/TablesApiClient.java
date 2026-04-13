package com.mws.wiki.integration;

import com.mws.wiki.model.dto.TableMetadata;
import com.mws.wiki.model.dto.TableRow;
import com.mws.wiki.model.dto.TableSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TablesApiClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${mws.tables.api.url}")
    private String tablesApiUrl;

    private WebClient getClient(String authToken) {
        return webClientBuilder
                .baseUrl(tablesApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();
    }

    public Mono<TableMetadata> getTableMetadata(String tableId, String authToken) {
        return getClient(authToken).get()
                .uri("/tables/{tableId}", tableId)
                .retrieve()
                .bodyToMono(TableMetadata.class);
    }

    public Mono<List<TableRow>> getTableRows(String tableId, String viewId, String authToken) {
        return getClient(authToken).get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tables/{tableId}/rows")
                        .queryParam("viewId", viewId)
                        .build(tableId))
                .retrieve()
                .bodyToFlux(TableRow.class)
                .collectList();
    }

    public Mono<TableSearchResponse> searchTables(String spaceId, String query, String authToken) {
        return getClient(authToken).get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tables/search")
                        .queryParam("spaceId", spaceId)
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(TableSearchResponse.class);
    }
}