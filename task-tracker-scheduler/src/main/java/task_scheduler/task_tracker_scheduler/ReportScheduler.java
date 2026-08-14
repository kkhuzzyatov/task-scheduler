package task_scheduler.task_tracker_scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.client.BackendClient;
import task_scheduler.task_tracker_scheduler.properties.InternalApiProperties;
import task_scheduler.task_tracker_scheduler.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportScheduler {

  private final BackendClient backendClient;
  private final LogProperties logProperties;
  private final InternalApiProperties internalApiProperties;

  @Scheduled(cron = "0 0 * * * *")
  public void sendReport() {
    backendClient.sendReports(internalApiProperties.apiKey());
    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "send_request_to_backend_for_returning_reports")
        .log("Backend internal endpoint is called");
  }
}
