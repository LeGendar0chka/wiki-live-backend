package com.mws.wiki.service;

import com.mws.wiki.model.dto.BacklinkDto;
import com.mws.wiki.model.dto.CreatePageRequest;
import com.mws.wiki.model.dto.PageSearchResultDto;
import com.mws.wiki.model.dto.WikiPageDto;
import com.mws.wiki.model.entity.PageLink;
import com.mws.wiki.model.entity.PageRevision;
import com.mws.wiki.model.entity.WikiPage;
import com.mws.wiki.repository.PageLinkRepository;
import com.mws.wiki.repository.PageRevisionRepository;
import com.mws.wiki.repository.WikiPageRepository;
import com.mws.wiki.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WikiPageService {

    private final WikiPageRepository wikiPageRepository;
    private final PageLinkRepository pageLinkRepository;
    private final PageLinkIndexer pageLinkIndexer;
    private final PageRevisionRepository revisionRepository;

    @Transactional
    public WikiPage createPage(CreatePageRequest request, String userId) {
        String slug = SlugGenerator.toSlug(request.getTitle());
        WikiPage page = WikiPage.builder()
                .spaceId(request.getSpaceId())
                .title(request.getTitle())
                .slug(slug)
                .contentJson(request.getContentJson())
                .contentText(extractText(request.getContentJson()))
                .status("draft")
                .createdBy(userId)
                .updatedBy(userId)
                .build();
        WikiPage saved = wikiPageRepository.save(page);
        pageLinkIndexer.rebuildLinksForPage(saved.getId(), saved.getContentJson());

        // Создаём первую ревизию
        createRevision(saved.getId(), request.getContentJson(), userId);

        return saved;
    }

    public WikiPage getPageById(UUID id) {
        return wikiPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));
    }

    @Transactional
    public WikiPage updatePageContent(UUID id, String contentJson, String userId) {
        WikiPage page = getPageById(id);
        page.setContentJson(contentJson);
        page.setContentText(extractText(contentJson));
        if (userId != null) {
            page.setUpdatedBy(userId);
        }
        WikiPage updated = wikiPageRepository.save(page);
        pageLinkIndexer.rebuildLinksForPage(id, contentJson);

        // Создаём новую ревизию
        createRevision(id, contentJson, userId);

        return updated;
    }

    public List<BacklinkDto> getBacklinks(UUID pageId) {
        List<PageLink> links = pageLinkRepository.findByTargetPageId(pageId);
        return links.stream().map(link -> {
            WikiPage source = wikiPageRepository.findById(link.getSourcePageId()).orElse(null);
            return BacklinkDto.builder()
                    .sourcePageId(link.getSourcePageId())
                    .sourcePageTitle(source != null ? source.getTitle() : "Unknown")
                    .anchorText(link.getAnchorText())
                    .build();
        }).collect(Collectors.toList());
    }

    public String getPageContentJson(String pageIdStr) {
        UUID id = UUID.fromString(pageIdStr);
        return getPageById(id).getContentJson();
    }

    // ========== НОВЫЕ МЕТОДЫ ДЛЯ ПОИСКА И СПИСКОВ ==========

    public List<PageSearchResultDto> searchPages(String query, String spaceId) {
        List<WikiPage> pages = wikiPageRepository
                .findBySpaceIdAndTitleContainingIgnoreCaseOrContentTextContainingIgnoreCase(
                        spaceId, query, query);
        return pages.stream()
                .map(p -> PageSearchResultDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .slug(p.getSlug())
                        .updatedAt(p.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<WikiPageDto> getPagesBySpace(String spaceId) {
        return wikiPageRepository.findBySpaceId(spaceId).stream()
                .map(WikiPageDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private String extractText(String json) {
        return json.replaceAll("\\<[^>]*>", "").replaceAll("\\[\\[.*?\\]\\]", "");
    }

    private void createRevision(UUID pageId, String contentJson, String userId) {
        int nextVersion = revisionRepository.countByPageId(pageId) + 1;
        PageRevision revision = PageRevision.builder()
                .pageId(pageId)
                .version(nextVersion)
                .snapshotJson(contentJson)
                .authorId(userId)
                .build();
        revisionRepository.save(revision);
    }
}