package task_scheduler.task_tracker_backend.dto.task;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TaskDto(UUID id, String title, String description, LocalDateTime completedAt) {}
