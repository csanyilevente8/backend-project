package com.example.todo.exception;

import java.util.UUID;

/**
 * Thrown when a Todo cannot be found for the given identifier.
 */
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(UUID id) {
        super("Todo not found with id: " + id);
    }
}
