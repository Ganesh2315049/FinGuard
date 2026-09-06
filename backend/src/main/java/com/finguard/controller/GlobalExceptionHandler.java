package com.finguard.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst()
                .orElse("Request validation failed");
        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<?> constraint(ConstraintViolationException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> illegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
        HttpStatus status = exception.getMessage() != null && exception.getMessage().contains("not found")
                ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        return error(status, exception.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> unexpected(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred", request);
    }

    private ResponseEntity<?> error(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now(), "status", status.value(), "error", status.getReasonPhrase(),
                "message", message == null ? status.getReasonPhrase() : message, "path", request.getRequestURI()));
    }
}
