package task_scheduler.task_tracker_summarization_service.dto;

import lombok.Builder;

@Builder
public record ReportResponse(String userEmail, String summary) {}
