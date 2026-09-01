package com.example.booksapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void resourceAccessExceptionReturnsServiceUnavailable() {
        ResponseEntity<Object> response = handler.handleResourceAccessException(
                new ResourceAccessException("connection timed out"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(503, body.get("status"));
        assertEquals("The book service is temporarily unavailable", body.get("message"));
    }
}
