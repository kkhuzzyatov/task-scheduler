package task_scheduler.task_tracker_summarization_service.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {

  private String userEmail;

  private List<TaskInfo> completedTasks;

  private List<TaskInfo> incompleteTasks;

  @Getter
  @NoArgsConstructor
  public static class TaskInfo {
    private String title;
    private String description;
  }
}
