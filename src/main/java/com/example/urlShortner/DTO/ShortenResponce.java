package com.example.urlShortner.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortenResponce{

    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private boolean customAlias;
    private Instant createdAt;
}