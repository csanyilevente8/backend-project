package com.example.todo.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response returned to API clients. Never contains stack traces
 * or internal implementation details.
 */
@Schema(description = "Standard API error response")
public record ApiError(

        @Schema(description = "Time the error occurred (UTC)", example = "2026-09-03T12:00:00Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Short error category", example = "Validation failed")
        String error,

        @Schema(description = "Human readable message", example = "Invalid request")
        String message,

        @Schema(description = "Field-specific validation errors, when applicable")
        Map<String, String> fieldErrors
) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, null);
    }

    public static ApiError of(int status, String error, String message, Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, fieldErrors);
    }
}
