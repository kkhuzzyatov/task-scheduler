package task_scheduler.task_tracker_backend.dto.report;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReportRequest {
  String userEmail;
  List<TaskSummaryDto> newTasksCreated;
  List<TaskSummaryDto> newCompletedTasks;
  List<TaskSummaryDto> newIncompleteTasks;
  List<TaskSummaryDto> tasksCompletedInPreviousReport;
  long timeSincePreviousReportSeconds;
}
