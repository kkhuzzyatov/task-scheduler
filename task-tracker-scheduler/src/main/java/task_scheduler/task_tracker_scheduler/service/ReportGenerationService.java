package task_scheduler.task_tracker_scheduler.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_scheduler.client.BackendClient;
import task_scheduler.task_tracker_scheduler.dto.ReportData;
import task_scheduler.task_tracker_scheduler.kafka.ReportRequestProducer;
import task_scheduler.task_tracker_scheduler.mapper.ReportRequestMapper;
import task_scheduler.task_tracker_scheduler.properties.InternalApiProperties;
import task_scheduler.task_tracker_scheduler.properties.LogProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerationService {

  private final BackendClient backendClient;
  private final ReportRequestMapper reportRequestMapper;
  private final ReportRequestProducer reportRequestProducer;

  private final InternalApiProperties internalApiProperties;
  private final LogProperties logProperties;

  public void generateReports(String executionId) {

    List<ReportData> reports = backendClient.getReports(internalApiProperties.apiKey());

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "report_users_loaded")
        .addKeyValue("usersCount", reports.size())
        .addKeyValue("executionId", executionId)
        .log("Users loaded for report");

    reports.stream()
        .filter(this::hasTasks)
        .peek(
            report ->
                log.atDebug()
                    .addKeyValue("service", logProperties.name())
                    .addKeyValue("event", "user_report_generation_started")
                    .addKeyValue("userId", report.userEmail())
                    .log("User report generation started"))
        .map(reportRequestMapper::toReportRequest)
        .forEach(reportRequestProducer::send);
  }

  private boolean hasTasks(ReportData report) {

    return !report.completedTasks().isEmpty() || !report.uncompletedTasks().isEmpty();
  }
}
