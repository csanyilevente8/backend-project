package com.example.todo.controller;

import com.example.todo.dto.NotificationResponse;
import com.example.todo.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Limit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Read-only notifications feed, populated by the {@code notifier} Kafka consumer
 * group (UC2 fan-out). Independent of the activity feed even though both consume
 * the same todo-events topic.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notifications fed from the 'notifier' Kafka consumer group")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Get recent notifications (newest first)")
    @GetMapping
    public List<NotificationResponse> recent(@RequestParam(defaultValue = "100") int limit) {
        if (limit <= 0 || limit > 500) {
            limit = 100;
        }
        return notificationRepository.findByOrderByIdDesc(Limit.of(limit)).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Operation(summary = "Count of unread notifications (for the UI bell badge)")
    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount() {
        return Map.of("count", notificationRepository.countByReadFalse());
    }

    @Operation(summary = "Mark all notifications as read")
    @PostMapping("/read")
    public Map<String, Integer> markAllRead() {
        return Map.of("updated", notificationRepository.markAllRead());
    }
}
