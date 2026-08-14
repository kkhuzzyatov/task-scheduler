package task_scheduler.task_tracker_summarization_service.kafka;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_service.dto.ReportRequest;
import task_scheduler.task_tracker_summarization_service.dto.ReportResponse;
import task_scheduler.task_tracker_summarization_service.dto.TaskSummaryDto;
import task_scheduler.task_tracker_summarization_service.properties.LogProperties;
import task_scheduler.task_tracker_summarization_service.service.SummaryGenerator;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportRequestConsumer {
  private final SummaryGenerator summaryGenerator;
  private final ReportResponseProducer reportResponseProducer;
  private final LogProperties logProperties;

  @KafkaListener(
      topics = "report-requests",
      containerFactory = "reportRequestKafkaListenerContainerFactory")
  public void consume(ReportRequest request) {

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "report_request_received")
        .addKeyValue("userId", request.getUserEmail())
        .log("Report request received from Kafka");

    String newTasksCreated = taskListToString(request.getNewTasksCreated());
    String newCompletedTasks = taskListToString(request.getNewCompletedTasks());
    String newIncompleteTasks = taskListToString(request.getNewIncompleteTasks());

    String summary;
    if (request.getTotalPreviousReportNumber() == 0) {
      summary =
          summaryGenerator.generateFirstReportSummary(
              request, newTasksCreated, newCompletedTasks, newIncompleteTasks);
    } else if (request.getTotalPreviousReportNumber() == 1) {
      summary =
          summaryGenerator.generateSecondReportSummary(
              request, newTasksCreated, newCompletedTasks, newIncompleteTasks);
    } else {
      summary =
          summaryGenerator.generateRegularReportSummary(
              request, newTasksCreated, newCompletedTasks, newIncompleteTasks);
    }

    ReportResponse reportResponse =
        ReportResponse.builder().userEmail(request.getUserEmail()).summary(summary).build();

    reportResponseProducer.send(reportResponse);
  }

  private String taskListToString(List<TaskSummaryDto> list) {
    if (list == null || list.isEmpty()) {
      return "No tasks";
    }

    return list.stream().map(TaskSummaryDto::toString).collect(Collectors.joining("\n"));
  }
}
