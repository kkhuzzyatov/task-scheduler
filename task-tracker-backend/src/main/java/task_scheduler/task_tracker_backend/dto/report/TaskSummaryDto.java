package task_scheduler.task_tracker_backend.dto.report;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryDto {
  private String title;
  private String description;
}
