package task_scheduler.task_tracker_scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class ReportScheduler {

  @Scheduled(fixedDelay = 40_000)
  public void sendReport() {}
}
