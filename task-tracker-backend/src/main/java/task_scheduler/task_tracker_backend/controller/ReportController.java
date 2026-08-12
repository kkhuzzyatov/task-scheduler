package task_scheduler.task_tracker_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_backend.kafka.ReportRequestProducer;
import task_scheduler.task_tracker_backend.service.AuthService;
import task_scheduler.task_tracker_backend.service.ReportService;
import task_scheduler.task_tracker_backend.user.User;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/report")
@Tag(name = "Report", description = "Report management")
public class ReportController {

  private final ReportService reportService;
  private final ReportRequestProducer reportRequestProducer;
  private final AuthService authService;

  private static final String STATUS_OK = "200";
  private static final String STATUS_UNAUTHORIZED = "401";
  private static final String STATUS_INTERNAL_SERVER_ERROR = "500";

  private static final String REPORT_RETRIEVED = "Report retrieved successfully";
  private static final String USER_NOT_AUTHENTICATED = "User is not authenticated";
  private static final String INTERNAL_SERVER_ERROR = "Internal server error";

  @Operation(summary = "Get report")
  @ApiResponses({
    @ApiResponse(responseCode = STATUS_OK, description = REPORT_RETRIEVED),
    @ApiResponse(responseCode = STATUS_UNAUTHORIZED, description = USER_NOT_AUTHENTICATED),
    @ApiResponse(responseCode = STATUS_INTERNAL_SERVER_ERROR, description = INTERNAL_SERVER_ERROR)
  })
  @GetMapping
  public ResponseEntity<Void> getReport(@RequestHeader("Authorization") String token) {
    UUID userId = authService.getMyUuid(token);
    User user = authService.getUserById(userId);

    reportRequestProducer.send(reportService.createReport(user.getEmail()));
    return ResponseEntity.ok().build();
  }
}
