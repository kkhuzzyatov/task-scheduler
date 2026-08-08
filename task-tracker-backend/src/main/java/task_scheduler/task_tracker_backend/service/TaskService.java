package task_scheduler.task_tracker_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_scheduler.task_tracker_backend.dto.task.CreateTaskRequest;
import task_scheduler.task_tracker_backend.dto.task.TaskDto;
import task_scheduler.task_tracker_backend.dto.task.UpdateTaskRequest;
import task_scheduler.task_tracker_backend.exception.TaskNotFoundException;
import task_scheduler.task_tracker_backend.exception.UserIsNotExistException;
import task_scheduler.task_tracker_backend.properties.LogProperties;
import task_scheduler.task_tracker_backend.task.Task;
import task_scheduler.task_tracker_backend.task.TaskRepository;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

  private static final String LOG_KEY_SERVICE = "service";
  private static final String LOG_KEY_EVENT = "event";
  private static final String LOG_KEY_USER_ID = "userId";
  private static final String LOG_KEY_TASK_ID = "taskId";

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final LogProperties logProperties;

  @Transactional(readOnly = true)
  public List<TaskDto> getTasks(UUID userId) {
    return taskRepository.findAllByUserUserIdAndDeletedAtIsNull(userId).stream()
        .map(this::mapToDto)
        .toList();
  }

  @Transactional
  public TaskDto createTask(UUID userId, CreateTaskRequest request) {

    User user = getUser(userId);

    Task task =
        Task.builder()
            .title(request.title())
            .description(request.description())
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();

    Task savedTask = taskRepository.save(task);

    log.atInfo()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "task_created")
        .addKeyValue(LOG_KEY_USER_ID, userId)
        .addKeyValue(LOG_KEY_TASK_ID, savedTask.getTaskId())
        .log("Task created");

    return mapToDto(savedTask);
  }

  @Transactional
  public TaskDto updateTask(UUID taskId, UUID userId, UpdateTaskRequest request) {

    Task task =
        taskRepository
            .findByTaskIdAndUserUserIdAndDeletedAtIsNull(taskId, userId)
            .orElseThrow(
                () -> {
                  log.atWarn()
                      .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
                      .addKeyValue(LOG_KEY_EVENT, "task_access_denied")
                      .addKeyValue(LOG_KEY_USER_ID, userId)
                      .addKeyValue(LOG_KEY_TASK_ID, taskId)
                      .log("Task access denied");

                  return new TaskNotFoundException();
                });

    List<String> changedFields = new ArrayList<>();

    if (request.title() != null) {
      task.setTitle(request.title());
      changedFields.add("title");
    }

    if (request.description() != null) {
      task.setDescription(request.description());
      changedFields.add("description");
    }

    if (request.completed() != null) {
      if (request.completed()) {
        task.complete();
      } else {
        task.uncomplete();
      }

      changedFields.add("completed");
    }

    log.atInfo()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "task_updated")
        .addKeyValue(LOG_KEY_USER_ID, userId)
        .addKeyValue(LOG_KEY_TASK_ID, taskId)
        .addKeyValue("changedFields", changedFields)
        .log("Task updated");

    return mapToDto(task);
  }

  @Transactional
  public void deleteTask(UUID taskId, UUID userId) {

    Task task =
        taskRepository
            .findByTaskIdAndUserUserIdAndDeletedAtIsNull(taskId, userId)
            .orElseThrow(
                () -> {
                  log.atWarn()
                      .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
                      .addKeyValue(LOG_KEY_EVENT, "task_access_denied")
                      .addKeyValue(LOG_KEY_USER_ID, userId)
                      .addKeyValue(LOG_KEY_TASK_ID, taskId)
                      .log("Task access denied");

                  return new TaskNotFoundException();
                });

    task.delete();

    log.atInfo()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "task_deleted")
        .addKeyValue(LOG_KEY_USER_ID, userId)
        .addKeyValue(LOG_KEY_TASK_ID, taskId)
        .log("Task deleted");
  }

  @Transactional(readOnly = true)
  public List<TaskDto> getCompletedTasksForPeriod(
      UUID userId, LocalDateTime from, LocalDateTime to) {

    return taskRepository
        .findAllByUserUserIdAndCompletedAtBetweenAndDeletedAtIsNull(userId, from, to)
        .stream()
        .map(this::mapToDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<TaskDto> getUncompletedTasks(UUID userId) {

    return taskRepository.findAllByUserUserIdAndCompletedAtIsNullAndDeletedAtIsNull(userId).stream()
        .map(this::mapToDto)
        .toList();
  }

  private User getUser(UUID userId) {
    return userRepository.findById(userId).orElseThrow(UserIsNotExistException::new);
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
