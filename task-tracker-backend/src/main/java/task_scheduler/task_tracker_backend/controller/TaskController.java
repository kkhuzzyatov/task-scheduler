package task_scheduler.task_tracker_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_backend.dto.task.CreateTaskRequest;
import task_scheduler.task_tracker_backend.dto.task.TaskDto;
import task_scheduler.task_tracker_backend.dto.task.UpdateTaskRequest;
import task_scheduler.task_tracker_backend.service.TaskService;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @Operation(summary = "Получить список задач текущего пользователя")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
  })
  @GetMapping
  public ResponseEntity<List<TaskDto>> getTasks(Principal principal) {
    UUID userId = UUID.fromString(principal.getName());

    return ResponseEntity.ok(taskService.getTasks(userId));
  }

  @Operation(summary = "Создать новую задачу")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
  })
  @PostMapping
  public ResponseEntity<TaskDto> createTask(
      @Valid @RequestBody CreateTaskRequest request, Principal principal) {

    UUID userId = UUID.fromString(principal.getName());

    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(userId, request));
  }

  @Operation(summary = "Обновить задачу")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
  })
  @PatchMapping("/{id}")
  public ResponseEntity<TaskDto> updateTask(
      @PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request, Principal principal) {

    UUID userId = UUID.fromString(principal.getName());

    return ResponseEntity.ok(taskService.updateTask(id, userId, request));
  }

  @Operation(summary = "Удалить задачу")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id, Principal principal) {

    UUID userId = UUID.fromString(principal.getName());

    taskService.deleteTask(id, userId);

    return ResponseEntity.noContent().build();
  }
}
