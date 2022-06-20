package com.example.search.service.search.searx;

import com.example.search.service.search.api.SearchResponse;
import com.example.search.service.search.api.SearchResult;
import com.example.search.service.search.api.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

/**
 * Сервис поиска через Searx
 *
 * @see <a href="https://habr.com/ru/post/545196/">Свободное API для поиска в интернете</a>
 * @see <a href="https://searx.space/">SearX online сервера</a>
 */
@Slf4j
public class SearXSearchService implements SearchService {
    private final HttpClient httpClient;
    private final Executor executor;
    private final ObjectMapper objectMapper;
    private final String serverUrl;
    private final Semaphore semaphore;

    public SearXSearchService(HttpClient httpClient, Executor executor, ObjectMapper objectMapper, String serverUrl, int maxConnections) {
        this.httpClient = httpClient;
        this.executor = executor;
        this.objectMapper = objectMapper;
        this.serverUrl = serverUrl;
        semaphore = new Semaphore(maxConnections);
    }

    @Override
    public SearchResponse search(List<String> queries, int queryResultsCount) {
        if (queries == null || queries.isEmpty()) {
            throw new IllegalArgumentException("Запросы обязательны!");
        }
        if (queries.size() == 1) {
            return search(queries.get(0), queryResultsCount);
        }
        List<CompletableFuture<SearchResponse>> futures = queries.stream()
                .skip(1)
                .map(query -> CompletableFuture.supplyAsync(() -> search(query, queryResultsCount), executor))
                .toList();
        final SearchResponse firstQueryResponse = search(queries.get(0), queryResultsCount);
        final Set<SearchResult> results = new LinkedHashSet<>(queries.size() * queryResultsCount);
        results.addAll(firstQueryResponse.results());
        futures.forEach(future -> results.addAll(future.join().results()));
        final SearchResponse searchResponse = new SearchResponse(String.join(" ", queries), results);
        log.debug("По запросу queries={}, queryResultCount={} получен ответ={}", queries, queryResultsCount, searchResponse);
        return searchResponse;
    }

    @Override
    public SearchResponse search(String query, int queryResultsCount) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Запрос обязателен!");
        }
        if (queryResultsCount <= 0) {
            throw new IllegalArgumentException("Количество результатов должно быть положительным!");
        }
        final SearXUriBuilder uriBuilder = SearXUriBuilder.searchUri(serverUrl, query);
        final HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();
        final Set<SearchResult> results = new LinkedHashSet<>(queryResultsCount);
        int foundResults;
        do {
            HttpRequest httpRequest = httpRequestBuilder.uri(uriBuilder.build()).build();
            final HttpResponse<String> httpResponse = sendRequest(httpRequest);
            final SearXSearchResponse searXResponse = parseResponse(httpResponse.body());
            foundResults = searXResponse.getResults().size();
            if (foundResults > 0) {
                final int copyResultsCount = Math.min(queryResultsCount - results.size(), foundResults);
                searXResponse.getResults().stream()
                        .limit(copyResultsCount)
                        .map(searXResult -> new SearchResult(searXResult.getUrl()))
                        .forEach(results::add);
                uriBuilder.nextPage();
            }
        } while (foundResults > 0 && results.size() < queryResultsCount);
        final SearchResponse searchResponse = new SearchResponse(query, results);
        log.debug("По запросу query={}, queryResultCount={} ответ={}", query, queryResultsCount, searchResponse);
        return searchResponse;
    }

    private HttpResponse<String> sendRequest(HttpRequest httpRequest) {
        final HttpResponse<String> response;
        try {
            semaphore.acquire();
            log.debug("Отправка http запроса {} в потоке {}",
                    httpRequest.uri(), Thread.currentThread().getName()
            );
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            log.debug("Получение http ответа на запрос {} в потоке {}",
                    httpRequest.uri(), Thread.currentThread().getName()
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Не удалось получить ответ searX", e);
        } finally {
            semaphore.release();
        }
        return response;
    }

    private SearXSearchResponse parseResponse(String json) {
        try {
            return objectMapper.readValue(json, SearXSearchResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Не удалось разобрать json ответ searX: " + json, e);
        }
    }
}
