package com.example.todo.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published to Kafka after a successful Todo mutation.
 *
 * <p>JSON shape is the cross-service contract (matches the Go backend):
 * {@code { "type", "todoId", "title", "completed", "timestamp" }}.</p>
 */
public record TodoEvent(
        String type,
        UUID todoId,
        String title,
        boolean completed,
        Instant timestamp
) {
    public static final String TOPIC = "todo-events";

    public static final String TYPE_CREATED = "TodoCreated";
    public static final String TYPE_UPDATED = "TodoUpdated";
    public static final String TYPE_COMPLETED = "TodoCompleted";
    public static final String TYPE_DELETED = "TodoDeleted";
}
