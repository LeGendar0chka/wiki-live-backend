package com.mws.wiki.service;

import com.mws.wiki.model.dto.RevisionDto;
import com.mws.wiki.model.dto.WikiPageDto;
import com.mws.wiki.model.entity.PageRevision;
import com.mws.wiki.model.entity.WikiPage;
import com.mws.wiki.repository.PageRevisionRepository;
import com.mws.wiki.repository.WikiPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevisionService {

    private final PageRevisionRepository revisionRepository;
    private final WikiPageRepository pageRepository;
    private final WikiPageService wikiPageService;

    public List<RevisionDto> getRevisions(UUID pageId) {
        return revisionRepository.findByPageIdOrderByVersionDesc(pageId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public WikiPageDto getPageAtRevision(UUID pageId, UUID revisionId) {
        PageRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new RuntimeException("Revision not found"));
        if (!revision.getPageId().equals(pageId)) {
            throw new RuntimeException("Revision does not belong to this page");
        }
        WikiPage page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found"));
        // Возвращаем DTO, подменяя contentJson на версию из ревизии
        WikiPageDto dto = WikiPageDto.fromEntity(page);
        dto.setContentJson(revision.getSnapshotJson());
        return dto;
    }

    @Transactional
    public WikiPageDto restoreRevision(UUID pageId, UUID revisionId, String userId) {
        PageRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new RuntimeException("Revision not found"));
        if (!revision.getPageId().equals(pageId)) {
            throw new RuntimeException("Revision does not belong to this page");
        }
        WikiPage updated = wikiPageService.updatePageContent(pageId, revision.getSnapshotJson(), userId);
        return WikiPageDto.fromEntity(updated);
    }

    private RevisionDto toDto(PageRevision entity) {
        return RevisionDto.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .snapshotJson(entity.getSnapshotJson())
                .authorId(entity.getAuthorId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}