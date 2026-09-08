package com.example.todo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * One entry in the activity_log, written by the Kafka consumer from a
 * {@code TodoEvent} and returned by GET /api/activity.
 */
@Entity
@Table(name = "activity_log")
public class ActivityEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "todo_id", nullable = false)
    private UUID todoId;

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "detail", length = 512)
    private String detail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ActivityEntry() {
    }

    public ActivityEntry(UUID todoId, String type, String detail) {
        this.todoId = todoId;
        this.type = type;
        this.detail = detail;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getTodoId() {
        return todoId;
    }

    public String getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
