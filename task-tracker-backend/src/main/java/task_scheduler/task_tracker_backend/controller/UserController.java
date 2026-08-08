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
import task_scheduler.task_tracker_backend.dto.user.RegisterRequest;
import task_scheduler.task_tracker_backend.dto.user.UserDto;
import task_scheduler.task_tracker_backend.service.AuthService;
import task_scheduler.task_tracker_backend.user.User;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management")
public class UserController {

  private static final String STATUS_OK = "200";
  private static final String STATUS_CREATED = "201";
  private static final String STATUS_BAD_REQUEST = "400";
  private static final String STATUS_UNAUTHORIZED = "401";
  private static final String STATUS_NOT_FOUND = "404";
  private static final String STATUS_CONFLICT = "409";
  private static final String STATUS_INTERNAL_SERVER_ERROR = "500";

  private static final String USER_REGISTERED = "User registered successfully";
  private static final String INVALID_REQUEST_DATA = "Invalid request data";
  private static final String USER_ALREADY_EXISTS = "User already exists";
  private static final String INTERNAL_SERVER_ERROR = "Internal server error";
  private static final String USER_DATA_RETRIEVED = "User data retrieved successfully";
  private static final String USER_NOT_AUTHENTICATED = "User is not authenticated";
  private static final String USER_NOT_FOUND = "User not found";

  private final AuthService authService;

  @Operation(summary = "Register a user")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_CREATED, description = USER_REGISTERED),
    @ApiResponse(responseCode = STATUS_BAD_REQUEST, description = INVALID_REQUEST_DATA),
    @ApiResponse(responseCode = STATUS_CONFLICT, description = USER_ALREADY_EXISTS),
    @ApiResponse(responseCode = STATUS_INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR)
  })
  @PostMapping
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request.email(), request.password());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "Get current user")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_OK, description = USER_DATA_RETRIEVED),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = USER_NOT_AUTHENTICATED),
    @ApiResponse(responseCode = STATUS_NOT_FOUND, description = USER_NOT_FOUND),
    @ApiResponse(responseCode = STATUS_INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR)
  })
  @GetMapping
  public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token) {
    UUID userId = authService.getMyUuid(token);

    User user = authService.getUserById(userId);

    UserDto userDto = UserDto.builder().id(user.getUserId()).email(user.getEmail()).build();

    return ResponseEntity.ok(userDto);
  }
}
