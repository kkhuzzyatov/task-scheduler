package task_scheduler.task_tracker_scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task_scheduler.task_tracker_scheduler.service.ReportGenerationService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportGenerationService reportGenerationService;

  @PostMapping
  public ResponseEntity<Void> generateReports() {

    reportGenerationService.generateReports();

    return ResponseEntity.accepted().build();
  }
}
