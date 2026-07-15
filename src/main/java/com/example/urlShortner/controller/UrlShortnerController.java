package com.example.urlShortner.controller;

import com.example.urlShortner.DTO.ShortenRequest;
import com.example.urlShortner.DTO.ShortenResponce;
import com.example.urlShortner.service.ShortUrlService;
import com.example.urlShortner.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UrlShortnerController {

    private final ShortUrlService service;
    private final AnalyticsService analyticsService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponce> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortenResponce response = service.shorten(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest httpRequest) {
        String originalUrl = service.resolve(code); // throws UrlNotFoundException -> 404 handled globally
        analyticsService.recordClick(code, httpRequest);

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302 — not cached by browsers, every click reaches the server
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}