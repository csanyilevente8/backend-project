package com.example.todo.dto;

import com.example.todo.entity.Todo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload representing a Todo returned to API clients.
 */
@Schema(description = "A Todo item")
public record TodoResponse(

        @Schema(description = "Unique identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Short title of the todo", example = "Learn Angular")
        String title,

        @Schema(description = "Optional longer description", example = "Complete Angular tutorial")
        String description,

        @Schema(description = "Whether the todo is completed", example = "false")
        boolean completed,

        @Schema(description = "Creation timestamp (UTC)", example = "2026-09-03T12:00:00Z")
        Instant createdAt,

        @Schema(description = "Last update timestamp (UTC)", example = "2026-09-03T12:00:00Z")
        Instant updatedAt
) {

    /**
     * Maps a {@link Todo} entity to its API response representation.
     */
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}
