package com.example.todo.service;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createPersistsTodoAndReturnsResponse() {
        CreateTodoRequest request = new CreateTodoRequest("Learn Angular", "Tutorial");
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        TodoResponse response = todoService.create(request);

        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Learn Angular");
        assertThat(response.title()).isEqualTo("Learn Angular");
        assertThat(response.completed()).isFalse();
    }

    @Test
    void findAllReturnsAllTodos() {
        Todo a = new Todo("A", "desc a");
        Todo b = new Todo("B", "desc b");
        when(todoRepository.findAll()).thenReturn(List.of(a, b));

        List<TodoResponse> result = todoService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TodoResponse::title).containsExactly("A", "B");
    }

    @Test
    void findByIdReturnsTodoWhenPresent() {
        UUID id = UUID.randomUUID();
        Todo todo = new Todo("A", "desc");
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        TodoResponse result = todoService.findById(id);

        assertThat(result.title()).isEqualTo("A");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(id))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void updateModifiesExistingTodo() {
        UUID id = UUID.randomUUID();
        Todo todo = new Todo("Old", "old desc");
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        TodoResponse result = todoService.update(id, new UpdateTodoRequest("New", "new desc", true));

        assertThat(result.title()).isEqualTo("New");
        assertThat(result.description()).isEqualTo("new desc");
        assertThat(result.completed()).isTrue();
    }

    @Test
    void updateThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.update(id, new UpdateTodoRequest("x", "y", false)))
                .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void updateCompletionChangesFlag() {
        UUID id = UUID.randomUUID();
        Todo todo = new Todo("A", "desc");
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        TodoResponse result = todoService.updateCompletion(id, true);

        assertThat(result.completed()).isTrue();
    }

    @Test
    void deleteRemovesExistingTodo() {
        UUID id = UUID.randomUUID();
        Todo todo = new Todo("A", "desc");
        when(todoRepository.findById(id)).thenReturn(Optional.of(todo));

        todoService.delete(id);

        verify(todoRepository).delete(todo);
    }

    @Test
    void deleteThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.delete(id))
                .isInstanceOf(TodoNotFoundException.class);
        verify(todoRepository, never()).delete(any());
    }
}
