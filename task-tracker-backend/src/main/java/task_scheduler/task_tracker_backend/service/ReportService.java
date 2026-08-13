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

  private static final LocalDateTime FIRST_REPORT_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);

  private final ReportRepository reportRepository;
  private final UserRepository userRepository;

  @Transactional
  public ReportRequest createReport(String userEmail) {

    User user =
        userRepository
            .findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    int totalPreviousReportNumber = reportRepository.countReportsByUserId(user.getUserId());

    List<TaskSummaryDto> newTasksCreated;
    List<TaskSummaryDto> newCompletedTasks;
    List<TaskSummaryDto> newIncompleteTasks;
    List<TaskSummaryDto> tasksCompletedInPreviousReport;

    long timeSincePreviousReportSeconds;

    if (totalPreviousReportNumber == 0) {

      LocalDateTime firstReportDate = LocalDateTime.of(1970, 1, 1, 0, 0);

      newTasksCreated =
          reportRepository.findTasksCreatedSince(user.getUserId(), firstReportDate).stream()
              .map(this::toSummary)
              .toList();

      newCompletedTasks =
          reportRepository.findCompletedTasksSince(user.getUserId(), firstReportDate).stream()
              .map(this::toSummary)
              .toList();

      newIncompleteTasks =
          reportRepository
              .findIncompleteTasksCreatedSince(user.getUserId(), firstReportDate)
              .stream()
              .map(this::toSummary)
              .toList();

      tasksCompletedInPreviousReport = List.of();

      timeSincePreviousReportSeconds = 0;

    } else if (totalPreviousReportNumber == 1) {

      LocalDateTime freshestReportCreatedAt =
          reportRepository.findFreshestReportCreatedAt(user.getUserId());

      newTasksCreated =
          reportRepository.findTasksCreatedSince(user.getUserId(), freshestReportCreatedAt).stream()
              .map(this::toSummary)
              .toList();

      newCompletedTasks =
          reportRepository
              .findCompletedTasksSince(user.getUserId(), freshestReportCreatedAt)
              .stream()
              .map(this::toSummary)
              .toList();

      newIncompleteTasks =
          reportRepository
              .findIncompleteTasksCreatedSince(user.getUserId(), freshestReportCreatedAt)
              .stream()
              .map(this::toSummary)
              .toList();

      tasksCompletedInPreviousReport = List.of();

      timeSincePreviousReportSeconds =
          Duration.between(freshestReportCreatedAt, LocalDateTime.now()).getSeconds();

    } else {

      LocalDateTime freshestReportCreatedAt =
          reportRepository.findFreshestReportCreatedAt(user.getUserId());

      LocalDateTime previousReportCreatedAt =
          reportRepository.findPreviousReportCreatedAt(user.getUserId());

      newTasksCreated =
          reportRepository.findTasksCreatedSince(user.getUserId(), freshestReportCreatedAt).stream()
              .map(this::toSummary)
              .toList();

      newCompletedTasks =
          reportRepository
              .findCompletedTasksSince(user.getUserId(), freshestReportCreatedAt)
              .stream()
              .map(this::toSummary)
              .toList();

      newIncompleteTasks =
          reportRepository
              .findIncompleteTasksCreatedSince(user.getUserId(), freshestReportCreatedAt)
              .stream()
              .map(this::toSummary)
              .toList();

      tasksCompletedInPreviousReport =
          reportRepository
              .findCompletedTasksBetweenReports(
                  user.getUserId(), previousReportCreatedAt, freshestReportCreatedAt)
              .stream()
              .map(this::toSummary)
              .toList();

      timeSincePreviousReportSeconds =
          Duration.between(freshestReportCreatedAt, LocalDateTime.now()).getSeconds();
    }

    reportRepository.save(Report.builder().user(user).createdAt(LocalDateTime.now()).build());

    return ReportRequest.builder()
        .userEmail(user.getEmail())
        .newTasksCreated(newTasksCreated)
        .newCompletedTasks(newCompletedTasks)
        .newIncompleteTasks(newIncompleteTasks)
        .tasksCompletedInPreviousReport(tasksCompletedInPreviousReport)
        .timeSincePreviousReportSeconds(timeSincePreviousReportSeconds)
        .totalPreviousReportNumber(totalPreviousReportNumber)
        .build();
  }

  private TaskSummaryDto toSummary(Task task) {
    return TaskSummaryDto.builder()
        .title(task.getTitle())
        .description(task.getDescription())
        .build();
  }
}
