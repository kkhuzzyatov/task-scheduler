package task_scheduler.task_tracker_summarization_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryDto {
  private String title;
  private String description;
}
