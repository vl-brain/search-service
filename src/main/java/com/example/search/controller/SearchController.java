package com.example.search.controller;

import com.example.search.service.search.api.SearchResponse;
import com.example.search.service.search.api.SearchService;
import com.example.search.service.stat.api.SearchStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class SearchController {
    private final SearchService searchService;
    private final SearchStatService searchStatService;

    public SearchController(SearchService searchService, SearchStatService searchStatService) {
        this.searchService = searchService;
        this.searchStatService = searchStatService;
    }

    @GetMapping("/search")
    public Map<String, Long> search(@RequestParam("query") List<String> queries) {
        SearchResponse response = searchService.search(queries, 100);
        Map<String, Long> domainResultsCount = searchStatService.countSecondLevelDomainResults(response);
        log.info("На запрос query={} получен ответ {}", queries, domainResultsCount);
        return domainResultsCount;
    }
}
