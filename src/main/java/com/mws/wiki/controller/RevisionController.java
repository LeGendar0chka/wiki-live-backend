package com.mws.wiki.controller;

import com.mws.wiki.model.dto.RevisionDto;
import com.mws.wiki.model.dto.WikiPageDto;
import com.mws.wiki.service.RevisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wiki/pages/{pageId}/revisions")
@RequiredArgsConstructor
public class RevisionController {

    private final RevisionService revisionService;

    @GetMapping
    public ResponseEntity<List<RevisionDto>> getRevisions(@PathVariable UUID pageId) {
        return ResponseEntity.ok(revisionService.getRevisions(pageId));
    }

    @GetMapping("/{revisionId}")
    public ResponseEntity<WikiPageDto> getRevision(
            @PathVariable UUID pageId,
            @PathVariable UUID revisionId) {
        return ResponseEntity.ok(revisionService.getPageAtRevision(pageId, revisionId));
    }

    @PostMapping("/{revisionId}/restore")
    public ResponseEntity<WikiPageDto> restoreRevision(
            @PathVariable UUID pageId,
            @PathVariable UUID revisionId,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(revisionService.restoreRevision(pageId, revisionId, userId));
    }
}