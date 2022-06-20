package com.example.search.service.search.searx;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Строитель запросов в searX
 */
public class SearXUriBuilder {
    private static final String SEARCH_PATH = "/search";
    private static final String DEFAULT_QUERY_FORMAT = "json";
    public static final String DEFAULT_CATEGORIES = String.join(",", "news", "general");

    private final String url;
    private final String query;
    private String categories;
    private int pageNumber = 1;
    private String format;

    private SearXUriBuilder(String url, String query) {
        this.url = url;
        this.query = query;
        categories = DEFAULT_CATEGORIES;
        format = DEFAULT_QUERY_FORMAT;
    }

    public static SearXUriBuilder searchUri(String serverUrl, String query) {
        return new SearXUriBuilder(serverUrl + SEARCH_PATH, query);
    }

    public SearXUriBuilder nextPage() {
        pageNumber++;
        return this;
    }

    public URI build() {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(SearXQueryParam.FORMAT.value, format)
                .queryParam(SearXQueryParam.CATEGORIES.value, categories)
                .queryParam(SearXQueryParam.QUERY.value, query)
                .queryParam(SearXQueryParam.PAGE_NUMBER.value, pageNumber)
                .build()
                .toUri();
    }


    /**
     * Параметр запроса в searX
     *
     * @see <a href="https://searx.github.io/searx/dev/search_api.html">SearX search API</a>
     */
    private enum SearXQueryParam {
        QUERY("q"),
        CATEGORIES("categories"),
        PAGE_NUMBER("pageno"),
        FORMAT("format"),
        ;
        private final String value;

        SearXQueryParam(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
