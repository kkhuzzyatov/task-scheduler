package task_scheduler.task_tracker_scheduler.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_scheduler.client.BackendClient;
import task_scheduler.task_tracker_scheduler.dto.DailyReportData;
import task_scheduler.task_tracker_scheduler.kafka.ReportRequestProducer;
import task_scheduler.task_tracker_scheduler.mapper.ReportRequestMapper;
import task_scheduler.task_tracker_scheduler.properties.InternalApiProperties;

@Service
@RequiredArgsConstructor
public class ReportGenerationService {

  private final BackendClient backendClient;
  private final ReportRequestMapper reportRequestMapper;
  private final ReportRequestProducer reportRequestProducer;

  private final InternalApiProperties internalApiProperties;

  public void generateReports() {

    List<DailyReportData> reports = backendClient.getDailyReports(internalApiProperties.apiKey());

    reports.stream()
        .filter(this::hasTasks)
        .map(reportRequestMapper::toReportRequest)
        .forEach(reportRequestProducer::send);
  }

  private boolean hasTasks(DailyReportData report) {

    return !report.completedTasks().isEmpty() || !report.uncompletedTasks().isEmpty();
  }
}
