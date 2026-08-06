package task_scheduler.task_tracker_email_sender.storage;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_email_sender.dto.ReportResponse;

@Component
public class TemporaryConsumedMessagesStorage {

  private final List<String> messages = new ArrayList<>();

  private final List<ReportResponse> reports = new ArrayList<>();

  public void addMessage(String message) {
    messages.add(message);
  }

  public List<String> getMessages() {
    return new ArrayList<>(messages);
  }

  public void addReport(ReportResponse report) {
    reports.add(report);
  }

  public List<ReportResponse> getReportsByEmail(String email) {
    return reports.stream().filter(report -> report.userEmail().equals(email)).toList();
  }
}
