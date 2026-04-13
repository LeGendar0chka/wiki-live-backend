package com.mws.wiki.controller;

import com.mws.wiki.model.dto.*;
import com.mws.wiki.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wiki")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/pages/{pageId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable UUID pageId) {
        return ResponseEntity.ok(commentService.getCommentsByPage(pageId));
    }

    @PostMapping("/pages/{pageId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable UUID pageId,
            @RequestBody CreateCommentRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(commentService.createComment(pageId, request, userId));
    }

    @PostMapping("/comments/{threadId}/replies")
    public ResponseEntity<CommentDto> createReply(
            @PathVariable UUID threadId,
            @RequestBody CreateReplyRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(commentService.createReply(threadId, request, userId));
    }

    @PatchMapping("/comments/{threadId}")
    public ResponseEntity<CommentDto> updateThreadStatus(
            @PathVariable UUID threadId,
            @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.updateStatus(threadId, request.getStatus()));
    }

    @GetMapping("/pages/{pageId}/comment-history")
    public ResponseEntity<List<CommentDto>> getCommentHistory(@PathVariable UUID pageId) {
        return ResponseEntity.ok(commentService.getCommentsByPage(pageId));
    }
}