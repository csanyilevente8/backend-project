package com.example.todo.events;

import com.example.todo.entity.ActivityEntry;
import com.example.todo.repository.ActivityRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@link TodoEvent}s (group {@code activity-logger}) and writes each to
 * the activity_log table. Only active when Kafka is configured
 * ({@code spring.kafka.bootstrap-servers}).
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class TodoEventConsumer {

    private final ActivityRepository activityRepository;

    public TodoEventConsumer(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @KafkaListener(topics = TodoEvent.TOPIC, groupId = "activity-logger")
    public void onEvent(TodoEvent event) {
        activityRepository.save(new ActivityEntry(event.todoId(), event.type(), describe(event)));
    }

    private String describe(TodoEvent e) {
        return switch (e.type()) {
            case TodoEvent.TYPE_CREATED -> "Created: " + e.title();
            case TodoEvent.TYPE_UPDATED -> "Updated: " + e.title();
            case TodoEvent.TYPE_COMPLETED -> (e.completed() ? "Marked complete: " : "Marked incomplete: ") + e.title();
            case TodoEvent.TYPE_DELETED -> "Deleted: " + e.title();
            default -> e.title();
        };
    }
}
