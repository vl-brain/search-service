package com.example.search.service.url.api;

import java.util.Optional;

/**
 * Сервис для работы с url
 */
public interface UrlService {
    /**
     * Получить домен 2 уровня для хоста
     *
     * @param url URL
     * @return домен, если есть
     */
    Optional<String> getHostSecondLevelDomain(String url);
}
