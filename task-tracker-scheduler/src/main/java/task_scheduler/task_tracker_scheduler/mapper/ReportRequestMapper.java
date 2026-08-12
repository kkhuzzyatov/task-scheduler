package task_scheduler.task_tracker_scheduler.mapper;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.dto.ReportData;
import task_scheduler.task_tracker_scheduler.dto.ReportRequest;
import task_scheduler.task_tracker_scheduler.dto.TaskDto;
import task_scheduler.task_tracker_scheduler.properties.LogProperties;

@Slf4j
@Component
public class ReportRequestMapper {

  private final LogProperties logProperties;

  public ReportRequestMapper(LogProperties logProperties) {
    this.logProperties = logProperties;
  }

  public ReportRequest toReportRequest(ReportData data) {

    ReportRequest request =
        ReportRequest.builder()
            .userEmail(data.userEmail())
            .completedTasks(mapTasks(data.completedTasks()))
            .incompleteTasks(mapTasks(data.uncompletedTasks()))
            .build();

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "report_request_created")
        .addKeyValue("userId", data.userEmail())
        .addKeyValue("tasksCount", data.completedTasks().size() + data.uncompletedTasks().size())
        .log("Report request prepared");

    return request;
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
