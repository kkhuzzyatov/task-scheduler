package task_scheduler.task_tracker_summarization_service.kafka;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_service.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_service.dto.ReportRequest;
import task_scheduler.task_tracker_summarization_service.dto.ReportResponse;
import task_scheduler.task_tracker_summarization_service.dto.TaskSummaryDto;
import task_scheduler.task_tracker_summarization_service.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportRequestConsumer {

  private final OpenAiApiClient openAiApiClient;
  private final ReportResponseProducer reportResponseProducer;
  private final LogProperties logProperties;

  @KafkaListener(
      topics = "report-requests",
      containerFactory = "reportRequestKafkaListenerContainerFactory")
  public void consume(ConsumerRecord<String, ReportRequest> record) {

    ReportRequest request = record.value();

    String messageId =
        String.format("%s-%s-%s", record.topic(), record.partition(), record.offset());

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "report_request_received")
        .addKeyValue("topic", record.topic())
        .addKeyValue("userId", request.getUserEmail())
        .addKeyValue("messageId", messageId)
        .log("Report request received from Kafka");

    String newTasksCreated = taskListToString(request.getNewTasksCreated());
    String newCompletedTasks = taskListToString(request.getNewTasksCreated());
    String newIncompleteTasks = taskListToString(request.getNewTasksCreated());
    String tasksCompletedInPreviousReport = taskListToString(request.getNewTasksCreated());

    String prompt =
        """
        User:
        %s
        New tasks created since the previous report:
        %s
        New completed tasks:
        %s
        New incomplete tasks:
        %s
        Tasks completed in the previous report:
        %s
        Time since previous report (seconds, rounded):
        %s
        Write a 3-5 sentence report:
        Progress: summarize completed work and outcomes since the previous report.
        Dynamics: compare activity with the previous period using available task data and elapsed time. State whether progress increased, decreased, stayed stable, or changed focus.
        Closing: give a brief observation about current momentum or task management.
        Rules:
        Be concise and professional.
        Focus on outcomes, not task lists.
        Do not invent missing information.
        Do not assume unavailable old task data exists.
        If there is insufficient activity data, state that clearly.
        """
            .formatted(
                request.getUserEmail(),
                newTasksCreated,
                newCompletedTasks,
                newIncompleteTasks,
                tasksCompletedInPreviousReport,
                request.getTimeSincePreviousReportSeconds());

    String summary = openAiApiClient.generateSummary(prompt, request.getUserEmail());

    ReportResponse reportResponse =
        ReportResponse.builder().userEmail(request.getUserEmail()).summary(summary).build();

    reportResponseProducer.send(reportResponse);
  }

  private String taskListToString(List<TaskSummaryDto> list) {
    return list.stream()
        .map(task -> "- %s: %s".formatted(task.getTitle(), task.getDescription()))
        .collect(Collectors.joining("\n"));
  }
}
