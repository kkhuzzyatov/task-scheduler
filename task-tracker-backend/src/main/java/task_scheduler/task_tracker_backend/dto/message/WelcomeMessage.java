package task_scheduler.task_tracker_backend.dto.message;

import lombok.Builder;

@Builder
public record WelcomeMessage(String email) {}
