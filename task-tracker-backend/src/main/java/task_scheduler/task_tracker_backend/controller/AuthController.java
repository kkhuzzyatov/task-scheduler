package task_scheduler.task_tracker_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_backend.dto.auth.AuthResponse;
import task_scheduler.task_tracker_backend.dto.auth.LoginRequest;
import task_scheduler.task_tracker_backend.dto.auth.LoginResult;
import task_scheduler.task_tracker_backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Аутентификация пользователей")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Вход пользователя")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "успешная авторизация"),
    @ApiResponse(responseCode = "400", description = "невалидные данные запроса"),
    @ApiResponse(responseCode = "401", description = "неверный email или пароль"),
    @ApiResponse(responseCode = "500", description = "неизвестная ошибка")
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResult result = authService.login(request.email(), request.password());
    return ResponseEntity.ok(new AuthResponse(result.token()));
  }
}
