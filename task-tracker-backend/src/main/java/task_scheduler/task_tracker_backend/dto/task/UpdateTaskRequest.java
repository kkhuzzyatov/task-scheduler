package task_scheduler.task_tracker_backend.dto.task;

import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
    @Size(min = 1, max = 255, message = "Task title must contain between 1 and 255 characters")
        String title,
    @Size(max = 5000, message = "Task description must not exceed 5000 characters")
        String description,
    Boolean completed) {}
