package com.example.todo.controller;

import com.example.todo.dto.ActivityResponse;
import com.example.todo.repository.ActivityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Limit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only activity feed, populated by the Kafka consumer. */
@RestController
@RequestMapping("/api/activity")
@Tag(name = "Activity", description = "Recent todo activity, fed from Kafka events")
public class ActivityController {

    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Operation(summary = "Get recent activity (newest first)")
    @GetMapping
    public List<ActivityResponse> recent(@RequestParam(defaultValue = "100") int limit) {
        if (limit <= 0 || limit > 500) {
            limit = 100;
        }
        return activityRepository.findByOrderByIdDesc(Limit.of(limit)).stream()
                .map(ActivityResponse::from)
                .toList();
    }
}
