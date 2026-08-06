package task_scheduler.task_tracker_scheduler.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import task_scheduler.task_tracker_scheduler.dto.DailyReportData;

@FeignClient(name = "backend-client", url = "${backend.url}")
public interface BackendClient {

  @GetMapping("/internal/reports/daily")
  List<DailyReportData> getDailyReports();
}
