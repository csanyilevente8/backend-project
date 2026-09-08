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
 * One entry in the notifications table, written by the {@code notifier} Kafka
 * consumer group from a {@code TodoEvent} and returned by GET /api/notifications.
 *
 * <p>This is the UC2 fan-out demo: the same todo-events feed both this table and
 * the activity_log, via two independent consumer groups.</p>
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "todo_id", nullable = false)
    private UUID todoId;

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "message", nullable = false, length = 512)
    private String message;

    @Column(name = "read", nullable = false)
    private boolean read;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Notification() {
    }

    public Notification(UUID todoId, String type, String message) {
        this.todoId = todoId;
        this.type = type;
        this.message = message;
        this.read = false;
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

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
