package com.mws.wiki.integration;

import com.mws.wiki.model.dto.GptRequest;
import com.mws.wiki.model.dto.GptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MwsGptClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${mws.gpt.api.url}")
    private String gptApiUrl;

    @Value("${mws.gpt.api.key}")
    private String apiKey;

    public Mono<String> generateCompletion(String prompt, String context) {
        WebClient client = webClientBuilder
                .baseUrl(gptApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        GptRequest request = GptRequest.builder()
                .prompt(prompt)
                .context(context)
                .maxTokens(500)
                .build();

        return client.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GptResponse.class)
                .map(GptResponse::getText);
    }
}