package com.example.urlShortner.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponce {

    private String message;
    private int status;
    private Instant timestamp;
    private List<String> details;

    public static ErrorResponce of(String message, int status) {
        return new ErrorResponce(message, status, Instant.now(), Collections.emptyList());
    }

    public static ErrorResponce of(String message, int status, List<String> details) {
        return new ErrorResponce(message, status, Instant.now(), details);
    }
}