package task_scheduler.task_tracker_scheduler.dto;

import java.util.List;

public record ReportData(
    String userEmail, List<TaskDto> completedTasks, List<TaskDto> uncompletedTasks) {
  public ReportData {
    completedTasks = List.copyOf(completedTasks);
    uncompletedTasks = List.copyOf(uncompletedTasks);
  }
}
