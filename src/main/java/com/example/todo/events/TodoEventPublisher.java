package com.example.todo.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes {@link TodoEvent}s to Kafka. Best-effort / fire-and-forget: a
 * failure is logged and never propagated, so the CRUD write path is unaffected.
 *
 * <p>The {@link KafkaTemplate} is injected via {@link ObjectProvider}, so if
 * Kafka is not configured (no {@code spring.kafka.bootstrap-servers}) the
 * template is absent and publishing becomes a no-op — the app runs unchanged
 * without Kafka.</p>
 */
@Component
public class TodoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TodoEventPublisher.class);

    private final ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplate;

    public TodoEventPublisher(ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TodoEvent event) {
        KafkaTemplate<String, Object> template = kafkaTemplate.getIfAvailable();
        if (template == null) {
            return; // Kafka not configured — no-op.
        }
        try {
            // Key by todo id to preserve per-todo ordering across partitions.
            template.send(TodoEvent.TOPIC, event.todoId().toString(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to publish {} event: {}", event.type(), ex.getMessage());
                        }
                    });
        } catch (Exception ex) {
            log.warn("Error publishing {} event: {}", event.type(), ex.getMessage());
        }
    }
}
