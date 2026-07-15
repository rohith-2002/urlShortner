package com.example.urlShortner.controller;

import com.example.urlShortner.DTO.ShortenRequest;
import com.example.urlShortner.DTO.ShortenResponce;
import com.example.urlShortner.service.ShortUrlService;
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

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponce> shorten(@Valid @RequestBody ShortenRequest request) {
        ShortenResponce response = service.shorten(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String originalUrl = service.resolve(code); // throws UrlNotFoundException -> 404 handled globally

        return ResponseEntity
                .status(HttpStatus.FOUND) // 302 — not cached by browsers, every click reaches the server
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }
}