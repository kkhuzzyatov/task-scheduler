package task_scheduler.task_tracker_summarization_service.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
  private String userEmail;
  private List<TaskSummaryDto> newTasksCreated;
  private List<TaskSummaryDto> newCompletedTasks;
  private List<TaskSummaryDto> newIncompleteTasks;
  private List<TaskSummaryDto> tasksCompletedInPreviousReport;
  private long timeSincePreviousReportSeconds;
  private int totalPreviousReportNumber;
}
