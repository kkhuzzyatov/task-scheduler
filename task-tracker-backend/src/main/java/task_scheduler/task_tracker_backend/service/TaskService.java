package task_scheduler.task_tracker_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task_scheduler.task_tracker_backend.dto.task.CreateTaskRequest;
import task_scheduler.task_tracker_backend.dto.task.TaskDto;
import task_scheduler.task_tracker_backend.dto.task.UpdateTaskRequest;
import task_scheduler.task_tracker_backend.exception.TaskNotFoundException;
import task_scheduler.task_tracker_backend.exception.UserIsNotExistException;
import task_scheduler.task_tracker_backend.task.Task;
import task_scheduler.task_tracker_backend.task.TaskRepository;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;

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

    return mapToDto(taskRepository.save(task));
  }

  @Transactional
  public TaskDto updateTask(UUID taskId, UUID userId, UpdateTaskRequest request) {

    Task task =
        taskRepository
            .findByTaskIdAndUserUserIdAndDeletedAtIsNull(taskId, userId)
            .orElseThrow(TaskNotFoundException::new);

    if (request.title() != null) {
      task.setTitle(request.title());
    }

    if (request.description() != null) {
      task.setDescription(request.description());
    }

    if (request.completed() != null) {

      if (request.completed()) {
        task.complete();
      } else {
        task.uncomplete();
      }
    }

    return mapToDto(task);
  }

  @Transactional
  public void deleteTask(UUID taskId, UUID userId) {

    Task task =
        taskRepository
            .findByTaskIdAndUserUserIdAndDeletedAtIsNull(taskId, userId)
            .orElseThrow(TaskNotFoundException::new);

    task.delete();
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
