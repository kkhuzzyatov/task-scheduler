package task_scheduler.task_tracker_summarization_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_service.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_service.dto.ReportRequest;

@RequiredArgsConstructor
@Component
public class SummaryGenerator {

  private final OpenAiApiClient openAiApiClient;

  public String generateFirstReportSummary(
      ReportRequest request,
      String newTasksCreated,
      String newCompletedTasks,
      String newIncompleteTasks) {

    String prompt =
        """
                User:
                %s

                This is the first generated report for this user.

                Tasks created:
                %s

                Tasks completed:
                %s

                Current incomplete tasks:
                %s

                Write a 3-5 sentence report:
                Progress: summarize current activity and completed work.
                Status: describe the current workload based on available task data.
                Closing: give a brief observation about the user's current momentum.

                Rules:
                Be concise and professional.
                Do not compare with previous periods.
                Do not invent missing information.
                If there is insufficient activity data, state that clearly.
                """
            .formatted(
                request.getUserEmail(), newTasksCreated, newCompletedTasks, newIncompleteTasks);

    return openAiApiClient.generateSummary(prompt, request.getUserEmail());
  }

  public String generateSecondReportSummary(
      ReportRequest request,
      String newTasksCreated,
      String newCompletedTasks,
      String newIncompleteTasks) {

    String prompt =
        """
                User:
                %s

                This is the second generated report for this user.
                Only one previous reporting period exists.

                New tasks created since previous report:
                %s

                New completed tasks:
                %s

                New incomplete tasks:
                %s

                Time since previous report (seconds, rounded):
                %s

                Write a 3-5 sentence report:
                Progress: summarize completed work and current activity.
                Dynamics: compare activity with the previous reporting period.
                Closing: give a brief observation about current task management.

                Rules:
                Be concise and professional.
                Use only available task data.
                Do not assume older historical data exists.
                Do not invent missing information.
                If there is insufficient activity data, state that clearly.
                """
            .formatted(
                request.getUserEmail(),
                newTasksCreated,
                newCompletedTasks,
                newIncompleteTasks,
                request.getTimeSincePreviousReportSeconds());

    return openAiApiClient.generateSummary(prompt, request.getUserEmail());
  }

  public String generateRegularReportSummary(
      ReportRequest request,
      String newTasksCreated,
      String newCompletedTasks,
      String newIncompleteTasks) {

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

                Tasks completed in the previous report period:
                %s

                Time since previous report (seconds, rounded):
                %s

                Write a 3-5 sentence report:
                Progress: summarize completed work and outcomes since the previous report.
                Dynamics: compare activity with the previous period using available task data and elapsed time.
                State whether progress increased, decreased, stayed stable, or changed focus.
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
                taskListToString(request),
                request.getTimeSincePreviousReportSeconds());

    return openAiApiClient.generateSummary(prompt, request.getUserEmail());
  }

  private String taskListToString(ReportRequest request) {
    if (request.getTasksCompletedInPreviousReport() == null
        || request.getTasksCompletedInPreviousReport().isEmpty()) {
      return "No tasks";
    }

    return request.getTasksCompletedInPreviousReport().stream()
        .map(Object::toString)
        .reduce("", (a, b) -> a + b + "\n");
  }
}
