package task_scheduler.task_tracker_scheduler.dto;

import java.util.List;

public record DailyReportData(
    String userEmail, List<TaskDto> completedTasks, List<TaskDto> uncompletedTasks) {
  public DailyReportData {
    completedTasks = List.copyOf(completedTasks);
    uncompletedTasks = List.copyOf(uncompletedTasks);
  }
}
