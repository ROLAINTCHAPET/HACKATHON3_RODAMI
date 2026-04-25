package com.rodami.campuslink.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNotFound(ResourceNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBadRequest(IllegalArgumentException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody(HttpStatus.BAD_REQUEST, ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGeneric(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur")));
    }

    private Map<String, Object> errorBody(HttpStatus status, String message) {
        return Map.of(
            "timestamp", Instant.now().toString(),
            "status",    status.value(),
            "error",     status.getReasonPhrase(),
            "message",   message
        );
    }
}
