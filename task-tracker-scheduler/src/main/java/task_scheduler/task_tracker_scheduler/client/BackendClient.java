package task_scheduler.task_tracker_scheduler.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "backend-client", url = "${backend.url}")
public interface BackendClient {

  @GetMapping("/internal/reports")
  void sendReports(@RequestHeader("X-Internal-Api-Key") String apiKey);
}
