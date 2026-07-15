package com.example.urlShortner.service;

import com.example.urlShortner.DTO.ShortenRequest;
import com.example.urlShortner.DTO.ShortenResponce;
import com.example.urlShortner.service.CounterService;
import com.example.urlShortner.entity.ShortUrl;
import com.example.urlShortner.exception.DuplicateAliasException;
import com.example.urlShortner.exception.UrlNotFoundException;
import com.example.urlShortner.repositories.ShortUrlRepository;
import com.example.urlShortner.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShortUrlService  {

    private final ShortUrlRepository repository;
    private final CounterService counterService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public ShortenResponce shorten(ShortenRequest request) {
        String hash = sha256(request.url());
        boolean isCustom = request.customAlias() != null && !request.customAlias().isBlank();

        // Duplicate-URL policy: reuse the existing short code for a URL
        // that's already been shortened — but ONLY when the caller didn't
        // explicitly request a custom alias. A custom alias is treated as
        // an intentional request for a distinct new mapping, even to an
        // already-known URL (e.g. a marketing campaign wanting its own
        // branded alias to a URL that also has a generic short code).
        if (!isCustom) {
            Optional<ShortUrl> existing = repository.findByOriginalUrlHash(hash);
            if (existing.isPresent()) {
                return toResponse(existing.get());
            }
        }

        String shortCode = isCustom
                ? resolveCustomAlias(request.customAlias())
                : generateFromCounter();

        ShortUrl entity = ShortUrl.builder()
                .shortCode(shortCode)
                .originalUrl(request.url())
                .originalUrlHash(hash)
                .customAlias(isCustom)
                .build();

        repository.save(entity);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public String resolve(String shortCode) {
        ShortUrl entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for code: " + shortCode));
        return entity.getOriginalUrl();
    }

    private String resolveCustomAlias(String alias) {
        if (repository.existsByShortCode(alias)) {
            throw new DuplicateAliasException("Alias '" + alias + "' is already taken");
        }
        return alias;
    }

    private String generateFromCounter() {
        long counterValue = counterService.nextValue();
        String code = Base62Encoder.encode(counterValue);

        // Defensive only — the sequence + Base62 combination is
        // collision-proof by construction (see write-up). This guards
        // against operational anomalies only (e.g. someone manually
        // resetting the sequence), not against the algorithm itself.
        if (repository.existsByShortCode(code)) {
            throw new IllegalStateException(
                    "Unexpected collision on generated code '" + code +
                            "' — the sequence may be out of sync with existing data"
            );
        }
        return code;
    }

    private ShortenResponce toResponse(ShortUrl entity) {
        return new ShortenResponce(
                entity.getShortCode(),
                baseUrl + "/" + entity.getShortCode(),
                entity.getOriginalUrl(),
                entity.isCustomAlias(),
                entity.getCreatedAt()
        );
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }
}
