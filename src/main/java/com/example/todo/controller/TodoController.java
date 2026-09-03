package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.UpdateCompletionRequest;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.exception.ApiError;
import com.example.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST API for managing Todo items. Business logic is delegated entirely to
 * {@link TodoService}; this controller only handles HTTP concerns.
 */
@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todos", description = "Operations for managing todo items")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Operation(summary = "Create a new todo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Todo created"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request,
                                               UriComponentsBuilder uriBuilder) {
        TodoResponse created = todoService.create(request);
        URI location = uriBuilder.path("/api/todos/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get all todos")
    @ApiResponse(responseCode = "200", description = "List of todos")
    @GetMapping
    public List<TodoResponse> getAll() {
        return todoService.findAll();
    }

    @Operation(summary = "Get a single todo by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo found"),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable UUID id) {
        return todoService.findById(id);
    }

    @Operation(summary = "Update an existing todo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTodoRequest request) {
        return todoService.update(id, request);
    }

    @Operation(summary = "Update only the completion status of a todo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Completion updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/{id}/complete")
    public TodoResponse updateCompletion(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateCompletionRequest request) {
        return todoService.updateCompletion(id, request.completed());
    }

    @Operation(summary = "Delete a todo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo deleted"),
            @ApiResponse(responseCode = "404", description = "Todo not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        todoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
