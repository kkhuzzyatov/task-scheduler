package task_scheduler.task_tracker_scheduler.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TaskDto(UUID id, String title, String description, LocalDateTime completedAt) {}
