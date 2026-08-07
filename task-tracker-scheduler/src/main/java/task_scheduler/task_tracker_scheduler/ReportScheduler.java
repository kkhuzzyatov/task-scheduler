package task_scheduler.task_tracker_scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.service.ReportGenerationService;

@Component
@RequiredArgsConstructor
public class ReportScheduler {

  private final ReportGenerationService reportGenerationService;

  @Scheduled(fixedDelay = 5_000)
  public void sendReport() {
    System.out.println("Starting report generation");

    try {
      reportGenerationService.generateReports();
      System.out.println("Report generation finished");
    } catch (Exception e) {
      System.err.println("Report generation failed");
      e.printStackTrace();
    }
  }
}
