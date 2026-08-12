package task_scheduler.task_tracker_summarization_service.kafka;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_service.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_service.dto.ReportRequest;
import task_scheduler.task_tracker_summarization_service.dto.ReportResponse;
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

    String messageId = record.topic() + "-" + record.partition() + "-" + record.offset();

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "report_request_received")
        .addKeyValue("topic", record.topic())
        .addKeyValue("userId", request.getUserEmail())
        .addKeyValue("messageId", messageId)
        .log("Report request received from Kafka");

    String completedTasks =
        request.getCompletedTasks().stream()
            .map(task -> "- %s: %s".formatted(task.getTitle(), task.getDescription()))
            .collect(Collectors.joining("\n"));

    String incompleteTasks =
        request.getIncompleteTasks().stream()
            .map(task -> "- %s: %s".formatted(task.getTitle(), task.getDescription()))
            .collect(Collectors.joining("\n"));

    String prompt =
        """
            Ты являешься помощником для создания ежедневного отчета по задачам пользователя.

            Составь краткий и структурированный отчет на русском языке.

            Пользователь: %s

            Выполненные задачи:
            %s

            Невыполненные задачи:
            %s

            Требования к отчету:
            - кратко опиши достигнутый прогресс;
            - перечисли основные выполненные задачи;
            - укажи оставшиеся задачи;
            - добавь небольшой итоговый комментарий.
            """
            .formatted(request.getUserEmail(), completedTasks, incompleteTasks);

    String summary = openAiApiClient.generateSummary(prompt, request.getUserEmail());

    ReportResponse reportResponse =
        ReportResponse.builder().userEmail(request.getUserEmail()).summary(summary).build();

    reportResponseProducer.send(reportResponse);
  }
}
