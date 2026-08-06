package task_scheduler.task_tracker_email_sender.dto;

import lombok.Builder;

@Builder
public record ReportResponse(String userEmail, String summary) {}
