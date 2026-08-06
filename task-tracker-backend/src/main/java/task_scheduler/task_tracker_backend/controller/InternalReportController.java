package task_scheduler.task_tracker_backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task_scheduler.task_tracker_backend.dto.report.DailyReportData;
import task_scheduler.task_tracker_backend.service.ReportService;

@RestController
@RequestMapping("/internal/reports")
@RequiredArgsConstructor
public class InternalReportController {

  private final ReportService reportService;

  @GetMapping("/daily")
  public List<DailyReportData> getDailyReports(@RequestHeader("X-Internal-Api-Key") String apiKey) {
    return reportService.getDailyReports();
  }
}
