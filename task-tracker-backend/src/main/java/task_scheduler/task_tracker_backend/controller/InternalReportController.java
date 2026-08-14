package task_scheduler.task_tracker_backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task_scheduler.task_tracker_backend.kafka.ReportRequestProducer;
import task_scheduler.task_tracker_backend.properties.LogProperties;
import task_scheduler.task_tracker_backend.service.ReportService;
import task_scheduler.task_tracker_backend.user.User;
import task_scheduler.task_tracker_backend.user.UserRepository;

@Slf4j
@RestController
@RequestMapping("/internal/reports")
@RequiredArgsConstructor
public class InternalReportController {

  private final ReportService reportService;
  private final LogProperties logProperties;
  private final UserRepository userRepository;
  private final ReportRequestProducer reportRequestProducer;

  @GetMapping
  public void getReports() {
    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "internal_report_request_received")
        .log("Internal API request received");

    List<User> users = userRepository.findAll();
    for (User user : users) {
      reportRequestProducer.send(reportService.createReport(user.getEmail()));
    }
  }
}
