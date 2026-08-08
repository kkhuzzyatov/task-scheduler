package task_scheduler.task_tracker_backend.dto.report;

import java.util.List;
import task_scheduler.task_tracker_backend.dto.task.TaskDto;

public record DailyReportData(
    String userEmail, List<TaskDto> completedTasks, List<TaskDto> uncompletedTasks) {

  public DailyReportData {
    completedTasks = List.copyOf(completedTasks);
    uncompletedTasks = List.copyOf(uncompletedTasks);
  }
}
