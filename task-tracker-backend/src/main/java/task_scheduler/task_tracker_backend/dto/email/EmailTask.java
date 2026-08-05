package task_scheduler.task_tracker_backend.dto.email;

import lombok.Builder;

@Builder
public record EmailTask(String recipient, String subject, String text) {}
