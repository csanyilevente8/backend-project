package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.UpdateTodoRequest;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private TodoResponse sampleResponse(UUID id, String title, boolean completed) {
        Instant now = Instant.parse("2026-09-03T12:00:00Z");
        return new TodoResponse(id, title, "desc", completed, now, now);
    }

    @Test
    void createReturns201() throws Exception {
        UUID id = UUID.randomUUID();
        when(todoService.create(any())).thenReturn(sampleResponse(id, "Learn Angular", false));

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTodoRequest("Learn Angular", "desc"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Learn Angular"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createWithBlankTitleReturns400() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateTodoRequest("", "desc"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void getAllReturns200WithArray() throws Exception {
        when(todoService.findAll()).thenReturn(List.of(
                sampleResponse(UUID.randomUUID(), "A", false),
                sampleResponse(UUID.randomUUID(), "B", true)));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getByIdReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(todoService.findById(id)).thenReturn(sampleResponse(id, "A", false));

        mockMvc.perform(get("/api/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getByIdReturns404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        when(todoService.findById(id)).thenThrow(new TodoNotFoundException(id));

        mockMvc.perform(get("/api/todos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void updateReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(todoService.update(eq(id), any())).thenReturn(sampleResponse(id, "Updated", true));

        mockMvc.perform(put("/api/todos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTodoRequest("Updated", "desc", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateCompletionReturns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(todoService.updateCompletion(eq(id), eq(true))).thenReturn(sampleResponse(id, "A", true));

        mockMvc.perform(patch("/api/todos/{id}/complete", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"completed\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void updateCompletionWithMissingFieldReturns400() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/todos/{id}/complete", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReturns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(todoService).delete(id);

        mockMvc.perform(delete("/api/todos/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteReturns404WhenMissing() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new TodoNotFoundException(id)).when(todoService).delete(id);

        mockMvc.perform(delete("/api/todos/{id}", id))
                .andExpect(status().isNotFound());
    }
}
