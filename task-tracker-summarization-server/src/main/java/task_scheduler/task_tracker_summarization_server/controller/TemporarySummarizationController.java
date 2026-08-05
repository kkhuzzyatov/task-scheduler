package task_scheduler.task_tracker_summarization_server.controller;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_summarization_server.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_server.dto.TaskReportGenerationRequest;
import task_scheduler.task_tracker_summarization_server.dto.TaskReportGenerationResponse;

@RestController
@RequestMapping("/api/summarization")
@RequiredArgsConstructor
public class TemporarySummarizationController {

  private final OpenAiApiClient openAiApiClient;

  @PostMapping
  public TaskReportGenerationResponse generate(@RequestBody TaskReportGenerationRequest request) {

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

    return new TaskReportGenerationResponse(summary);
  }
}
