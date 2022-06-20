package com.example.search.service.url.impl;

import com.example.search.service.url.api.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UrlServiceImpl implements UrlService {
    private static final Pattern DNS_SECOND_LEVEL_DOMAIN_PATTERN = Pattern.compile(
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]\\.[a-zA-Z]{2,63}$");

    @Override
    public Optional<String> getHostSecondLevelDomain(String url) {
        final String host;
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            log.error("Не удалось разобрать url={}", url, e);
            return Optional.empty();
        }
        final Matcher matcher = DNS_SECOND_LEVEL_DOMAIN_PATTERN.matcher(host);
        if (matcher.find()) {
            final String domain = matcher.group();
            log.debug("Для url={} получен домен 2 уровня {}", url, domain);
            return Optional.of(domain);
        }
        log.warn("Для url={} не удалось получить домен 2 уровня", url);
        return Optional.empty();
    }
}
