package task_scheduler.task_tracker_summarization_service.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
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
