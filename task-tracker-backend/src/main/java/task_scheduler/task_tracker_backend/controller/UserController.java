package task_scheduler.task_tracker_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_backend.dto.RegisterRequest;
import task_scheduler.task_tracker_backend.service.AuthService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Управление пользователем")
public class UserController {

  private final AuthService authService;

  @Operation(summary = "Регистрация пользователя")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "пользователь зарегистрирован"),
    @ApiResponse(responseCode = "400", description = "невалидные данные запроса"),
    @ApiResponse(responseCode = "409", description = "пользователь уже существует"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request.email(), request.password());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "Получить текущего пользователя")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "данные пользователя"),
    @ApiResponse(responseCode = "401", description = "пользователь не авторизован"),
    @ApiResponse(responseCode = "404", description = "пользователь не найден"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @GetMapping
  public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token) {
    UUID userId = authService.getMyUuid(token);
    return ResponseEntity.ok(authService.getUserById(userId));
  }
}
