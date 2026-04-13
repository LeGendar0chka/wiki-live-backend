package com.mws.wiki.controller;

import com.mws.wiki.integration.MwsGptClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final MwsGptClient gptClient;

    @PostMapping("/assist")
    public Mono<String> assist(@RequestBody AssistRequest request) {
        return gptClient.generateCompletion(request.getPrompt(), request.getContext());
    }

    @Data
    static class AssistRequest {
        private String prompt;
        private String context;
    }
}