package task_scheduler.task_tracker_summarization_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_summarization_server.dto.TaskReportGenerationRequest;
import task_scheduler.task_tracker_summarization_server.dto.TaskReportGenerationResponse;

@RestController
@RequestMapping("/api/summarization")
@RequiredArgsConstructor
public class TemporarySummarizationController {

  @PostMapping
  public TaskReportGenerationResponse generate(@RequestBody TaskReportGenerationRequest request) {

    System.out.println("===== SUMMARIZATION REQUEST =====");
    System.out.println("User: " + request.getUserEmail());

    System.out.println("Completed tasks:");
    request.getCompletedTasks().forEach(task -> System.out.println("- " + task.getTitle()));

    System.out.println("Incomplete tasks:");
    request.getIncompleteTasks().forEach(task -> System.out.println("- " + task.getTitle()));

    System.out.println("==============================");

    return new TaskReportGenerationResponse("Fake summary generated for " + request.getUserEmail());
  }
}
