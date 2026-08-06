package task_scheduler.task_tracker_scheduler.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

  private String userEmail;

  private List<TaskInfo> completedTasks;

  private List<TaskInfo> incompleteTasks;

  @Builder
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TaskInfo {
    private String title;
    private String description;
  }
}
