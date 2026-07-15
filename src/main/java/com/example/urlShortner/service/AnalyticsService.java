package com.example.urlShortner.service;

import com.example.urlShortner.entity.ClickEvent;
import com.example.urlShortner.repositories.ClickEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ClickEventRepository clickEventRepository;

    @Async
    public void recordClick(String shortCode, HttpServletRequest request) {
        ClickEvent event = ClickEvent.builder()
                .shortCode(shortCode)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build();

        clickEventRepository.save(event);
    }

    public long getClickCount(String shortCode) {
        return clickEventRepository.countByShortCode(shortCode);
    }
}