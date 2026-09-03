package com.example.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload used to fully update an existing Todo.
 */
@Schema(description = "Payload for updating an existing Todo")
public record UpdateTodoRequest(

        @Schema(description = "Short title of the todo", example = "Learn Angular", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Title must not be empty")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Schema(description = "Optional longer description", example = "Complete Angular and RxJS tutorial")
        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Schema(description = "Whether the todo is completed", example = "true")
        boolean completed
) {
}
