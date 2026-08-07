package task_scheduler.task_tracker_scheduler;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.properties.LogProperties;
import task_scheduler.task_tracker_scheduler.service.ReportGenerationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

  private final ReportGenerationService reportGenerationService;
  private final LogProperties logProperties;

  @Scheduled(fixedDelay = 5_000)
  public void sendReport() {

    String executionId = UUID.randomUUID().toString();

    try {
      MDC.put("correlationId", executionId);

      log.atInfo()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "daily_report_generation_started")
          .addKeyValue("executionId", executionId)
          .log("Daily report job started");

      reportGenerationService.generateReports(executionId);

    } catch (Exception e) {

      log.atError()
          .setCause(e)
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "daily_report_generation_failed")
          .addKeyValue("executionId", executionId)
          .log("Daily report generation failed");

    } finally {
      MDC.remove("correlationId");
    }
  }
}
