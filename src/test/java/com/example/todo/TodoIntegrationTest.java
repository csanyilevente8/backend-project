package com.example.todo;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.dto.UpdateTodoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full-stack integration test running the application against a real
 * PostgreSQL instance managed by Testcontainers. Verifies Flyway migration,
 * persistence, and the REST layer end to end.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TodoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("todo")
            .withUsername("todo")
            .withPassword("todo");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void useJdkClientForPatchSupport() {
        // The default SimpleClientHttpRequestFactory (HttpURLConnection) does not
        // support the HTTP PATCH method. The JDK HttpClient factory does.
        restTemplate.getRestTemplate().setRequestFactory(new JdkClientHttpRequestFactory());
    }

    @Test
    void fullCrudLifecycle() {
        // Create
        ResponseEntity<TodoResponse> created = restTemplate.postForEntity(
                "/api/todos", new CreateTodoRequest("Learn Angular", "Tutorial"), TodoResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        TodoResponse todo = created.getBody();
        assertThat(todo).isNotNull();
        assertThat(todo.id()).isNotNull();
        assertThat(todo.completed()).isFalse();
        UUID id = todo.id();

        // Get by id
        ResponseEntity<TodoResponse> fetched = restTemplate.getForEntity("/api/todos/" + id, TodoResponse.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody().title()).isEqualTo("Learn Angular");

        // Update
        HttpEntity<UpdateTodoRequest> updateBody = jsonEntity(new UpdateTodoRequest("Learn Angular", "RxJS too", true));
        ResponseEntity<TodoResponse> updated = restTemplate.exchange(
                "/api/todos/" + id, HttpMethod.PUT, updateBody, TodoResponse.class);
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody().completed()).isTrue();
        assertThat(updated.getBody().description()).isEqualTo("RxJS too");

        // Patch completion back to false
        HttpEntity<String> patchBody = jsonEntity("{\"completed\": false}");
        ResponseEntity<TodoResponse> patched = restTemplate.exchange(
                "/api/todos/" + id + "/complete", HttpMethod.PATCH, patchBody, TodoResponse.class);
        assertThat(patched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patched.getBody().completed()).isFalse();

        // Delete
        ResponseEntity<Void> deleted = restTemplate.exchange(
                "/api/todos/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Confirm gone
        ResponseEntity<String> afterDelete = restTemplate.getForEntity("/api/todos/" + id, String.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void healthEndpointReportsUp() {
        ResponseEntity<String> health = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(health.getBody()).contains("UP");
    }

    private <T> HttpEntity<T> jsonEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
