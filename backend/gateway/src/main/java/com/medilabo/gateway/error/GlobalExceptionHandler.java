package com.medilabo.gateway.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ApiError(Instant timestamp, int status, String error, String path) {}

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        var body = new ApiError(Instant.now(), 404, "Not Found", req.getRequestURI());
        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBadRequest(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var body = new ApiError(Instant.now(), 400, "Bad Request", req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        var body = new ApiError(Instant.now(), 500, "Internal Server Error", req.getRequestURI());
        return ResponseEntity.status(500).body(body);
    }
}
