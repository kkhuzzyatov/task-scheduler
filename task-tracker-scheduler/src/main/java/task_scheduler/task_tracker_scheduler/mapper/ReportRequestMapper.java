package task_scheduler.task_tracker_scheduler.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.dto.DailyReportData;
import task_scheduler.task_tracker_scheduler.dto.ReportRequest;
import task_scheduler.task_tracker_scheduler.dto.TaskDto;

@Component
public class ReportRequestMapper {

  public ReportRequest toReportRequest(DailyReportData data) {

    return ReportRequest.builder()
        .userEmail(data.userEmail())
        .completedTasks(mapTasks(data.completedTasks()))
        .incompleteTasks(mapTasks(data.uncompletedTasks()))
        .build();
  }

  private List<ReportRequest.TaskInfo> mapTasks(List<TaskDto> tasks) {

    return tasks.stream()
        .map(
            task ->
                ReportRequest.TaskInfo.builder()
                    .title(task.title())
                    .description(task.description())
                    .build())
        .toList();
  }
}
