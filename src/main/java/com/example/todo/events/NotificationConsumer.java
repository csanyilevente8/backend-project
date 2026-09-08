package com.example.todo.events;

import com.example.todo.entity.Notification;
import com.example.todo.repository.NotificationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@link TodoEvent}s in consumer group {@code notifier} and writes a
 * notification row per event.
 *
 * <p>This is a SECOND, independent consumer group on the SAME todo-events topic
 * as {@link TodoEventConsumer} (group {@code activity-logger}). It demonstrates
 * Kafka fan-out (UC2): one event lands in both {@code activity_log} and
 * {@code notifications}, each group tracking its own offset. Only active when
 * Kafka is configured ({@code app.kafka.enabled=true}).</p>
 */
@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = TodoEvent.TOPIC, groupId = "notifier")
    public void onEvent(TodoEvent event) {
        String message = describe(event);
        if (message == null) {
            return; // event type this consumer doesn't notify on
        }
        notificationRepository.save(new Notification(event.todoId(), event.type(), message));
    }

    /**
     * Builds the notification message, or returns {@code null} for event types
     * that produce no notification. For the learning demo we notify on
     * created/completed/deleted and skip plain updates.
     */
    private String describe(TodoEvent e) {
        return switch (e.type()) {
            case TodoEvent.TYPE_CREATED -> "New todo added: " + e.title();
            case TodoEvent.TYPE_COMPLETED -> (e.completed() ? "Todo completed: " : "Todo reopened: ") + e.title();
            case TodoEvent.TYPE_DELETED -> "Todo deleted: " + e.title();
            default -> null;
        };
    }
}
