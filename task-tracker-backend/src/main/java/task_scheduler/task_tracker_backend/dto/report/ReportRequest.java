package task_scheduler.task_tracker_backend.dto.report;

import java.util.List;
import lombok.*;

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
