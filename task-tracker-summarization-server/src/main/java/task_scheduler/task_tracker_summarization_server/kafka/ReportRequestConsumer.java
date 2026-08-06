package task_scheduler.task_tracker_summarization_server.kafka;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_server.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_server.dto.ReportRequest;
import task_scheduler.task_tracker_summarization_server.dto.ReportResponse;

@Component
@RequiredArgsConstructor
public class ReportRequestConsumer {

  private final OpenAiApiClient openAiApiClient;
  private final ReportResponseProducer reportResponseProducer;

  @KafkaListener(topics = "report-requests")
  public void consume(ReportRequest request) {

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

    String summary = openAiApiClient.generateSummary(prompt);

    ReportResponse reportResponse =
        ReportResponse.builder().userEmail(request.getUserEmail()).summary(summary).build();

    reportResponseProducer.send(reportResponse);
  }
}
