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

  private static final String STATUS_OK = "200";
  private static final String STATUS_CREATED = "201";
  private static final String STATUS_NO_CONTENT = "204";
  private static final String STATUS_BAD_REQUEST = "400";
  private static final String STATUS_UNAUTHORIZED = "401";
  private static final String STATUS_NOT_FOUND = "404";

  private static final String TASK_LIST_RETRIEVED = "Task list retrieved successfully";
  private static final String TASK_CREATED = "Task created successfully";
  private static final String TASK_UPDATED = "Task updated successfully";
  private static final String TASK_DELETED = "Task deleted successfully";
  private static final String INVALID_DATA = "Invalid data provided";
  private static final String USER_NOT_AUTHENTICATED = "User is not authenticated";
  private static final String TASK_NOT_FOUND = "Task not found";

  private final TaskService taskService;

  @Operation(summary = "Get current user's task list")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_OK, description = TASK_LIST_RETRIEVED),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = USER_NOT_AUTHENTICATED)
  })
  @GetMapping
  public ResponseEntity<List<TaskDto>> getTasks(Principal principal) {
    UUID userId = UUID.fromString(principal.getName());

    return ResponseEntity.ok(taskService.getTasks(userId));
  }

  @Operation(summary = "Create a new task")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_CREATED, description = TASK_CREATED),
    @ApiResponse(responseCode = STATUS_BAD_REQUEST, description = INVALID_DATA),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = USER_NOT_AUTHENTICATED)
  })
  @PostMapping
  public ResponseEntity<TaskDto> createTask(
      @Valid @RequestBody CreateTaskRequest request, Principal principal) {

    UUID userId = UUID.fromString(principal.getName());

    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(userId, request));
  }

  @Operation(summary = "Update a task")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_OK, description = TASK_UPDATED),
    @ApiResponse(responseCode = STATUS_BAD_REQUEST, description = INVALID_DATA),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = USER_NOT_AUTHENTICATED),
    @ApiResponse(responseCode = STATUS_NOT_FOUND, description = TASK_NOT_FOUND)
  })
  @PatchMapping("/{id}")
  public ResponseEntity<TaskDto> updateTask(
      @PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request, Principal principal) {

    UUID userId = UUID.fromString(principal.getName());

    return ResponseEntity.ok(taskService.updateTask(id, userId, request));
  }

  @Operation(summary = "Delete a task")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_NO_CONTENT, description = TASK_DELETED),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = USER_NOT_AUTHENTICATED),
    @ApiResponse(responseCode = STATUS_NOT_FOUND, description = TASK_NOT_FOUND)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable UUID id, Principal principal) {

    UUID userId = UUID.fromString(principal.getName());

    taskService.deleteTask(id, userId);

    return ResponseEntity.noContent().build();
  }
}
