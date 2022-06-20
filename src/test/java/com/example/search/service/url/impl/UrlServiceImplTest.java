package com.example.search.service.url.impl;

import com.example.search.service.url.api.UrlService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UrlServiceImplTest {

    private final UrlService service = new UrlServiceImpl();

    @Test
    void getHostSecondLevelDomainForDnsName() {
        Optional<String> domain = service.getHostSecondLevelDomain("https://www.mirror.co.uk/test/123");
        assertTrue(domain.isPresent());
        assertEquals("co.uk", domain.get());
    }

    @Test
    void getHostSecondLevelDomainForIpV4Name() {
        Optional<String> domain = service.getHostSecondLevelDomain("http://127.0.0.1/test/123");
        assertTrue(domain.isEmpty());
    }

    @Test
    void getHostSecondLevelDomainForIpV6Name() {
        Optional<String> domain = service.getHostSecondLevelDomain("http://[::1]/test/123");
        assertTrue(domain.isEmpty());
    }
}