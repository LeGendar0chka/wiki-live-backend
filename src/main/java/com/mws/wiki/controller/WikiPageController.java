package com.mws.wiki.controller;

import com.mws.wiki.model.dto.BacklinkDto;
import com.mws.wiki.model.dto.CreatePageRequest;
import com.mws.wiki.model.dto.UpdatePageRequest;
import com.mws.wiki.model.dto.WikiPageDto;
import com.mws.wiki.model.entity.WikiPage;
import com.mws.wiki.service.WikiPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mws.wiki.model.dto.PageSearchResultDto;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wiki/pages")
@RequiredArgsConstructor
@Slf4j
public class WikiPageController {

    private final WikiPageService wikiPageService;

    @PostMapping
    public ResponseEntity<WikiPageDto> createPage(@RequestBody CreatePageRequest request,
                                                  @RequestHeader(value = "X-User-Id", required = false) String userId) {
        WikiPage page = wikiPageService.createPage(request, userId);
        return ResponseEntity.ok(WikiPageDto.fromEntity(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WikiPageDto> getPage(@PathVariable UUID id) {
        WikiPage page = wikiPageService.getPageById(id);
        return ResponseEntity.ok(WikiPageDto.fromEntity(page));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WikiPageDto> updatePage(@PathVariable UUID id,
                                                  @RequestBody UpdatePageRequest request,
                                                  @RequestHeader(value = "X-User-Id", required = false) String userId) {
        WikiPage updated = wikiPageService.updatePageContent(id, request.getContentJson(), userId);
        return ResponseEntity.ok(WikiPageDto.fromEntity(updated));
    }

    @GetMapping("/{id}/backlinks")
    public ResponseEntity<List<BacklinkDto>> getBacklinks(@PathVariable UUID id) {
        return ResponseEntity.ok(wikiPageService.getBacklinks(id));
    }
    @GetMapping("/search")
    public ResponseEntity<List<PageSearchResultDto>> searchPages(
        @RequestParam String q,
        @RequestParam String spaceId) {
        return ResponseEntity.ok(wikiPageService.searchPages(q, spaceId));
    }

    @GetMapping
    public ResponseEntity<List<WikiPageDto>> getPagesBySpace(@RequestParam String spaceId) {
        return ResponseEntity.ok(wikiPageService.getPagesBySpace(spaceId));
    }
}