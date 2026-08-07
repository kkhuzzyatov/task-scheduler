package task_scheduler.task_tracker_email_sender.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_email_sender.dto.ReportResponse;
import task_scheduler.task_tracker_email_sender.storage.TemporaryConsumedMessagesStorage;

@Component
@RequiredArgsConstructor
public class ReportResponseConsumer {

  private final TemporaryConsumedMessagesStorage storage;

  @KafkaListener(
      topics = "report-response",
      containerFactory = "reportKafkaListenerContainerFactory")
  public void consume(ReportResponse response) {

    storage.addReport(response);
  }
}
