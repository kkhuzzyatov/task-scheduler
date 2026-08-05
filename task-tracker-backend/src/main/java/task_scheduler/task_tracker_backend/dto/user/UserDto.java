package task_scheduler.task_tracker_backend.dto.user;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserDto(UUID id, String email) {}
