package com.example.urlShortner.controller;
import com.example.urlShortner.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.urlShortner.DTO.AnalyticsResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{code}")
    public ResponseEntity<AnalyticsResponse> getClicks(@PathVariable String code) {
        long count = analyticsService.getClickCount(code);
        return ResponseEntity.ok(new AnalyticsResponse(code, count));
    }
}
