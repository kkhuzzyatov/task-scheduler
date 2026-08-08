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
@Tag(name = "Authentication", description = "User authentication")
public class AuthController {

  private static final String STATUS_OK = "200";
  private static final String STATUS_BAD_REQUEST = "400";
  private static final String STATUS_UNAUTHORIZED = "401";
  private static final String STATUS_INTERNAL_SERVER_ERROR = "500";

  private static final String AUTHENTICATION_SUCCESSFUL = "Authentication successful";
  private static final String INVALID_REQUEST_DATA = "Invalid request data";
  private static final String INVALID_CREDENTIALS = "Invalid email or password";
  private static final String INTERNAL_SERVER_ERROR = "Internal server error";

  private final AuthService authService;

  @Operation(summary = "User login")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_OK, description = AUTHENTICATION_SUCCESSFUL),
    @ApiResponse(responseCode = STATUS_BAD_REQUEST, description = INVALID_REQUEST_DATA),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = INVALID_CREDENTIALS),
    @ApiResponse(responseCode = STATUS_INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR)
  })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResult result = authService.login(request.email(), request.password());

    return ResponseEntity.ok(new AuthResponse(result.token()));
  }
}
