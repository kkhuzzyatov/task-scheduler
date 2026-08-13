package task_scheduler.task_tracker_backend.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_scheduler.task_tracker_backend.dto.report.ReportRequest;
import task_scheduler.task_tracker_backend.dto.report.TaskSummaryDto;
import task_scheduler.task_tracker_backend.report.Report;
import task_scheduler.task_tracker_backend.report.ReportRepository;
import task_scheduler.task_tracker_backend.task.Task;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final UserRepository userRepository;

  @Transactional
  public ReportRequest createReport(String userEmail) {

    User user =
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    LocalDateTime freshestReportTime =
        reportRepository.findFreshestReportCreatedAt(user.getUserId());

    long timeSincePreviousReportSeconds =
        freshestReportTime == null
            ? 0
            : Duration.between(freshestReportTime, LocalDateTime.now()).getSeconds();

    LocalDateTime freshestReportCreatedAt =
        reportRepository.findFreshestReportCreatedAt(user.getUserId());
    LocalDateTime previousReportCreatedAt =
        reportRepository.findPreviousReportCreatedAt(user.getUserId());

    List<TaskSummaryDto> newTasksCreated =
        reportRepository.findTasksCreatedSince(user.getUserId(), freshestReportCreatedAt).stream()
            .map(this::toSummary)
            .toList();

    List<TaskSummaryDto> newCompletedTasks =
        reportRepository.findCompletedTasksSince(user.getUserId(), freshestReportCreatedAt).stream()
            .map(this::toSummary)
            .toList();

    List<TaskSummaryDto> newIncompleteTasks =
        reportRepository
            .findIncompleteTasksCreatedSince(user.getUserId(), freshestReportCreatedAt)
            .stream()
            .map(this::toSummary)
            .toList();

    List<TaskSummaryDto> tasksCompletedInPreviousReport =
        reportRepository
            .findCompletedTasksBetweenReports(
                user.getUserId(), previousReportCreatedAt, freshestReportCreatedAt)
            .stream()
            .map(this::toSummary)
            .toList();

    // Create report after collecting previous data.
    reportRepository.save(Report.builder().user(user).createdAt(LocalDateTime.now()).build());

    return ReportRequest.builder()
        .userEmail(user.getEmail())
        .newTasksCreated(newTasksCreated)
        .newCompletedTasks(newCompletedTasks)
        .newIncompleteTasks(newIncompleteTasks)
        .tasksCompletedInPreviousReport(tasksCompletedInPreviousReport)
        .timeSincePreviousReportSeconds(timeSincePreviousReportSeconds)
        .build();
  }

  private TaskSummaryDto toSummary(Task task) {
    return TaskSummaryDto.builder()
        .title(task.getTitle())
        .description(task.getDescription())
        .build();
  }
}
