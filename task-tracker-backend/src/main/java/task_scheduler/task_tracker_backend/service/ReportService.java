package task_scheduler.task_tracker_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_scheduler.task_tracker_backend.dto.report.DailyReportData;
import task_scheduler.task_tracker_backend.dto.task.TaskDto;
import task_scheduler.task_tracker_backend.task.Task;
import task_scheduler.task_tracker_backend.task.TaskRepository;
import task_scheduler.task_tracker_backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final UserRepository userRepository;
  private final TaskRepository taskRepository;

  @Transactional(readOnly = true)
  public List<DailyReportData> getDailyReports() {

    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now();

    return userRepository.findAll().stream()
        .map(
            user ->
                new DailyReportData(
                    user.getEmail(),
                    taskRepository
                        .findAllByUserUserIdAndCompletedAtBetweenAndDeletedAtIsNull(
                            user.getUserId(), from, to)
                        .stream()
                        .map(this::mapToDto)
                        .toList(),
                    taskRepository
                        .findAllByUserUserIdAndCompletedAtIsNullAndDeletedAtIsNull(user.getUserId())
                        .stream()
                        .map(this::mapToDto)
                        .toList()))
        .toList();
  }

  private TaskDto mapToDto(Task task) {
    return TaskDto.builder()
        .id(task.getTaskId())
        .title(task.getTitle())
        .description(task.getDescription())
        .completedAt(task.getCompletedAt())
        .build();
  }
}
