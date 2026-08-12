package task_scheduler.task_tracker_backend.dto.report;

import java.util.List;
import task_scheduler.task_tracker_backend.dto.task.TaskDto;

public record ReportData(
    String userEmail, List<TaskDto> completedTasks, List<TaskDto> uncompletedTasks) {

  public ReportData {
    completedTasks = List.copyOf(completedTasks);
    uncompletedTasks = List.copyOf(uncompletedTasks);
  }
}
