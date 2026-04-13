package com.mws.wiki.service;

import com.mws.wiki.model.dto.CommentDto;
import com.mws.wiki.model.dto.CreateCommentRequest;
import com.mws.wiki.model.dto.CreateReplyRequest;
import com.mws.wiki.model.entity.CommentThread;
import com.mws.wiki.repository.CommentThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentThreadRepository commentRepository;

    public List<CommentDto> getCommentsByPage(UUID pageId) {
        return commentRepository.findByPageId(pageId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto createComment(UUID pageId, CreateCommentRequest request, String userId) {
        CommentThread comment = CommentThread.builder()
                .pageId(pageId)
                .blockId(request.getBlockId())
                .selection(request.getSelection())
                .content(request.getContent())
                .status("open")
                .createdBy(userId)
                .parentId(null)
                .build();
        return toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto createReply(UUID parentThreadId, CreateReplyRequest request, String userId) {
        CommentThread parent = commentRepository.findById(parentThreadId)
                .orElseThrow(() -> new RuntimeException("Parent thread not found"));

        CommentThread reply = CommentThread.builder()
                .pageId(parent.getPageId())
                .blockId(parent.getBlockId())
                .selection(parent.getSelection())
                .content(request.getContent())
                .status("open")
                .createdBy(userId)
                .parentId(parentThreadId)
                .build();
        return toDto(commentRepository.save(reply));
    }

    @Transactional
    public CommentDto updateStatus(UUID threadId, String status) {
        CommentThread comment = commentRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus(status);
        if ("resolved".equals(status)) {
            comment.setResolvedAt(Instant.now());
        }
        return toDto(commentRepository.save(comment));
    }

    private CommentDto toDto(CommentThread entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .pageId(entity.getPageId())
                .blockId(entity.getBlockId())
                .selection(entity.getSelection())
                .content(entity.getContent())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .resolvedAt(entity.getResolvedAt())
                .parentId(entity.getParentId())
                .build();
    }
}