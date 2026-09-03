package com.example.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to update only the completion status of a Todo.
 */
@Schema(description = "Payload for updating the completion status of a Todo")
public record UpdateCompletionRequest(

        @Schema(description = "Whether the todo is completed", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "completed must be provided")
        Boolean completed
) {
}
