package task_scheduler.task_tracker_backend.dto.report;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskSummaryDto {
  String title;
  String description;
}
