package task_scheduler.task_tracker_scheduler.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import task_scheduler.task_tracker_scheduler.dto.ReportData;

@FeignClient(name = "backend-client", url = "${backend.url}")
public interface BackendClient {

  @GetMapping("/internal/reports")
  List<ReportData> getReports(@RequestHeader("X-Internal-Api-Key") String apiKey);
}
