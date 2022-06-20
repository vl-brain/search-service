package com.example.search.service.stat.api;

import com.example.search.service.search.api.SearchResponse;

import java.util.Map;

/**
 * Сервис поисковой статистики
 */
public interface SearchStatService {

    Map<String, Long> countSecondLevelDomainResults(SearchResponse response);
}
