package com.example.urlShortner.exception;


import com.example.urlShortner.DTO.ErrorResponce;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponce> handleNotFound(UrlNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponce.of(ex.getMessage(), 404));
    }

    @ExceptionHandler(DuplicateAliasException.class)
    public ResponseEntity<ErrorResponce> handleDuplicateAlias(DuplicateAliasException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponce.of(ex.getMessage(), 409));
    }

    // Bean Validation failures on @Valid @RequestBody (e.g. invalid URL, bad alias pattern)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponce> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponce.of("Validation failed", 400, details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponce> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponce.of(ex.getMessage(), 400));
    }

    // Catch-all fallback — never leak stack traces to the client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponce> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponce.of("An unexpected error occurred", 500));
    }
}

