package com.example.search;

import com.example.search.service.search.api.SearchService;
import com.example.search.service.search.searx.SearXSearchService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

    @Bean
    public SearchService searchService() {
        return new SearXSearchService(
                HttpClient.newHttpClient(),
                Executors.newFixedThreadPool(3),
                new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false),
                "https://searx.ru",
                5
        );
    }
}
