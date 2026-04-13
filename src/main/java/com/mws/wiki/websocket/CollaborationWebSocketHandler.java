package com.mws.wiki.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mws.wiki.service.WikiPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CollaborationWebSocketHandler extends TextWebSocketHandler {

    private final WikiPageService wikiPageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // pageId -> set of sessions
    private final Map<String, Set<WebSocketSession>> pageSessions = new ConcurrentHashMap<>();
    // session -> pageId
    private final Map<String, String> sessionPageMap = new ConcurrentHashMap<>();
    // pageId -> последнее состояние документа
    private final Map<String, String> documentState = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String pageId = extractPageId(session);
        if (pageId == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        session.getAttributes().put("pageId", pageId);
        sessionPageMap.put(session.getId(), pageId);
        pageSessions.computeIfAbsent(pageId, k -> ConcurrentHashMap.newKeySet()).add(session);

        // Отправляем начальное состояние
        String content = wikiPageService.getPageContentJson(pageId);
        documentState.putIfAbsent(pageId, content);
        session.sendMessage(new TextMessage("{\"type\":\"init\",\"content\":" + objectMapper.writeValueAsString(content) + "}"));
        broadcastPresence(pageId, session.getId(), "joined");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String pageId = (String) session.getAttributes().get("pageId");
        if (pageId == null) return;

        JsonNode json = objectMapper.readTree(message.getPayload());
        String type = json.path("type").asText();

        if ("update".equals(type)) {
            String update = json.path("update").asText();
            documentState.put(pageId, update);
            broadcastToPage(pageId, session, message.getPayload());
            schedulePersist(pageId, update);
        } else if ("cursor".equals(type)) {
            broadcastToPage(pageId, session, message.getPayload());
        }
    }

    private void schedulePersist(String pageId, String content) {
        scheduler.schedule(() -> {
            try {
                UUID id = UUID.fromString(pageId);
                wikiPageService.updatePageContent(id, content, "system");
                log.debug("Auto-saved page {}", pageId);
            } catch (Exception e) {
                log.error("Failed to persist page {}", pageId, e);
            }
        }, 2, TimeUnit.SECONDS);
    }

    private void broadcastToPage(String pageId, WebSocketSession sender, String payload) {
        Set<WebSocketSession> sessions = pageSessions.get(pageId);
        if (sessions != null) {
            TextMessage msg = new TextMessage(payload);
            sessions.forEach(s -> {
                if (!s.getId().equals(sender.getId()) && s.isOpen()) {
                    try {
                        s.sendMessage(msg);
                    } catch (IOException e) {
                        log.error("Failed to send to session {}", s.getId(), e);
                    }
                }
            });
        }
    }

    private void broadcastPresence(String pageId, String sessionId, String event) {
        // При желании можно реализовать отправку событий присутствия
    }

    private String extractPageId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String pageId = sessionPageMap.remove(session.getId());
        if (pageId != null) {
            Set<WebSocketSession> sessions = pageSessions.get(pageId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    pageSessions.remove(pageId);
                    String content = documentState.remove(pageId);
                    if (content != null) {
                        wikiPageService.updatePageContent(UUID.fromString(pageId), content, "system");
                    }
                }
            }
        }
    }
}