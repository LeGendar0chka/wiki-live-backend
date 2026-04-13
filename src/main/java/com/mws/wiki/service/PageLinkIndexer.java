package com.mws.wiki.service;

import com.mws.wiki.model.entity.PageLink;
import com.mws.wiki.model.entity.WikiPage;
import com.mws.wiki.repository.PageLinkRepository;
import com.mws.wiki.repository.WikiPageRepository;
import com.mws.wiki.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageLinkIndexer {

    private final PageLinkRepository pageLinkRepository;
    private final WikiPageRepository wikiPageRepository;

    private static final Pattern LINK_PATTERN = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");

    @Transactional
    public void rebuildLinksForPage(UUID sourcePageId, String contentJson) {
        pageLinkRepository.deleteBySourcePageId(sourcePageId);
        Set<String> slugs = extractSlugs(contentJson);
        for (String slug : slugs) {
            Optional<WikiPage> targetOpt = wikiPageRepository.findBySlug(slug);
            if (targetOpt.isPresent()) {
                PageLink link = PageLink.builder()
                        .sourcePageId(sourcePageId)
                        .targetPageId(targetOpt.get().getId())
                        .anchorText(slug)
                        .build();
                pageLinkRepository.save(link);
            } else {
                log.debug("Target page with slug '{}' not found", slug);
            }
        }
    }

    private Set<String> extractSlugs(String content) {
        Set<String> slugs = new HashSet<>();
        Matcher matcher = LINK_PATTERN.matcher(content);
        while (matcher.find()) {
            String ref = matcher.group(1);
            slugs.add(SlugGenerator.toSlug(ref));
        }
        return slugs;
    }
}