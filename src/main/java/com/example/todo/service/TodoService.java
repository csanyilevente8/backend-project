package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.events.TodoEvent;
import com.example.todo.events.TodoEventPublisher;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final TodoEventPublisher eventPublisher;

    public TodoService(TodoRepository todoRepository, TodoEventPublisher eventPublisher) {
        this.todoRepository = todoRepository;
        this.eventPublisher = eventPublisher;
    }

    private void emit(String type, TodoResponse t) {
        eventPublisher.publish(new TodoEvent(type, t.id(), t.title(), t.completed(), Instant.now()));
    }

    public TodoResponse create(CreateTodoRequest request) {
        Todo todo = new Todo(request.title(), request.description());
        Todo saved = todoRepository.save(todo);
        TodoResponse resp = TodoResponse.from(saved);
        emit(TodoEvent.TYPE_CREATED, resp);
        return resp;
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
        TodoResponse resp = TodoResponse.from(todo);
        emit(TodoEvent.TYPE_UPDATED, resp);
        return resp;
    }

    public TodoResponse updateCompletion(UUID id, boolean completed) {
        Todo todo = getExisting(id);
        todo.setCompleted(completed);
        TodoResponse resp = TodoResponse.from(todo);
        emit(TodoEvent.TYPE_COMPLETED, resp);
        return resp;
    }

    public void delete(UUID id) {
        Todo todo = getExisting(id);
        TodoResponse resp = TodoResponse.from(todo);
        todoRepository.delete(todo);
        emit(TodoEvent.TYPE_DELETED, resp);
    }

    private Todo getExisting(UUID id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }
}
