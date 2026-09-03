package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Business logic for managing {@link Todo} items.
 *
 * <p>The service works with DTOs at its boundary so that JPA entities are never
 * exposed directly to the web layer.</p>
 */
@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse create(CreateTodoRequest request) {
        Todo todo = new Todo(request.title(), request.description());
        Todo saved = todoRepository.save(todo);
        return TodoResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findAll() {
        return todoRepository.findAll().stream()
                .map(TodoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse findById(UUID id) {
        Todo todo = getExisting(id);
        return TodoResponse.from(todo);
    }

    public TodoResponse update(UUID id, UpdateTodoRequest request) {
        Todo todo = getExisting(id);
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());
        // Managed entity is flushed on transaction commit; return current state.
        return TodoResponse.from(todo);
    }

    public TodoResponse updateCompletion(UUID id, boolean completed) {
        Todo todo = getExisting(id);
        todo.setCompleted(completed);
        return TodoResponse.from(todo);
    }

    public void delete(UUID id) {
        Todo todo = getExisting(id);
        todoRepository.delete(todo);
    }

    private Todo getExisting(UUID id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}
