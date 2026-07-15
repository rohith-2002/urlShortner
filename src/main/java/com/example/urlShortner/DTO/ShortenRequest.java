package com.example.urlShortner.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record ShortenRequest(
        @NotBlank(message = "URL must not be blank")
        @URL(message = "Must be a valid, well-formed URL")
        String url,

        @Pattern(
                regexp = "^[a-zA-Z0-9_-]{3,16}$",
                message = "Alias must be 3-16 characters, alphanumeric with - or _"
        )
        String customAlias
) {}