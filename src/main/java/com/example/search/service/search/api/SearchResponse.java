package com.example.search.service.search.api;

import java.util.Set;

/**
 * Ответ сервиса поиска
 *
 * @param query   запрос
 * @param results результаты
 */
public record SearchResponse(String query, Set<SearchResult> results) {
}
