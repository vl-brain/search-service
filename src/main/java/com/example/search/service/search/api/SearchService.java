package com.example.search.service.search.api;

import java.util.List;

/**
 * Сервис поиска
 */
public interface SearchService {
    /**
     * Поиск по списку запросов
     *
     * @param queries           список запросов
     * @param queryResultsCount количество результатов на запрос
     * @return ответ
     */
    SearchResponse search(List<String> queries, int queryResultsCount);

    /**
     * Поиск по запросу
     *
     * @param query             запрос
     * @param queryResultsCount количество результатов на запрос
     * @return ответ
     */
    SearchResponse search(String query, int queryResultsCount);
}
