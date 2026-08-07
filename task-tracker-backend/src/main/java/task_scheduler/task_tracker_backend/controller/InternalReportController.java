package task_scheduler.task_tracker_backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task_scheduler.task_tracker_backend.dto.report.DailyReportData;
import task_scheduler.task_tracker_backend.properties.LogProperties;
import task_scheduler.task_tracker_backend.service.ReportService;

@Slf4j
@RestController
@RequestMapping("/internal/reports")
@RequiredArgsConstructor
public class InternalReportController {

  private static final String CALLER_SERVICE_HEADER = "X-Caller-Service";

  private final ReportService reportService;
  private final LogProperties logProperties;

  @GetMapping("/daily")
  public List<DailyReportData> getDailyReports(
      @RequestHeader("X-Internal-Api-Key") String apiKey,
      @RequestHeader(value = CALLER_SERVICE_HEADER, required = false) String callerService) {

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "internal_report_request_received")
        .addKeyValue("callerService", callerService)
        .log("Internal API request received");

    return reportService.getDailyReports();
  }
}
