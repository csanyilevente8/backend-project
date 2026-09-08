package com.example.todo.dto;

import com.example.todo.entity.Notification;

import java.time.Instant;
import java.util.UUID;

/** Notification returned by GET /api/notifications. */
public record NotificationResponse(
        Long id,
        UUID todoId,
        String type,
        String message,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getTodoId(), n.getType(),
                n.getMessage(), n.isRead(), n.getCreatedAt());
    }
}
