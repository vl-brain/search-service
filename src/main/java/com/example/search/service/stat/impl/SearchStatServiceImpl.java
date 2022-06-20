package com.example.search.service.stat.impl;

import com.example.search.service.search.api.SearchResponse;
import com.example.search.service.search.api.SearchResult;
import com.example.search.service.stat.api.SearchStatService;
import com.example.search.service.url.api.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchStatServiceImpl implements SearchStatService {
    private final UrlService urlService;

    public SearchStatServiceImpl(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public Map<String, Long> countSecondLevelDomainResults(SearchResponse response) {
        Map<String, Long> domainToResultsCount = response.results().stream()
                .map(SearchResult::url)
                .map(urlService::getHostSecondLevelDomain)
                .filter(Optional::isPresent)
                .collect(Collectors.groupingBy(Optional::get, Collectors.counting()));
        log.debug("В поисковом ответе {} количетво найденных результатов по доменам 2 уровня {}",
                response, domainToResultsCount);
        return domainToResultsCount;
    }
}
