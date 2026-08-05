package task_scheduler.task_tracker_backend.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
    @NotBlank(message = "Task title must not be blank")
        @Size(max = 255, message = "Task title must not exceed 255 characters")
        String title,
    @Size(max = 5000, message = "Task description must not exceed 5000 characters")
        String description) {}
