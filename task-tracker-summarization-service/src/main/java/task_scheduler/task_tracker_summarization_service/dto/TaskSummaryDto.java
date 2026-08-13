package task_scheduler.task_tracker_summarization_service.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class TaskSummaryDto {
  String title;
  String description;
}
