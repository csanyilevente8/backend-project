package com.example.todo.dto;

import com.example.todo.entity.ActivityEntry;

import java.time.Instant;
import java.util.UUID;

/** Activity entry returned by GET /api/activity. */
public record ActivityResponse(
        Long id,
        UUID todoId,
        String type,
        String detail,
        Instant createdAt
) {
    public static ActivityResponse from(ActivityEntry e) {
        return new ActivityResponse(e.getId(), e.getTodoId(), e.getType(), e.getDetail(), e.getCreatedAt());
    }
}
