package com.example.search.service.search.searx;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearXSearchResponse {
    private List<SearXSearchResult> results;
}
