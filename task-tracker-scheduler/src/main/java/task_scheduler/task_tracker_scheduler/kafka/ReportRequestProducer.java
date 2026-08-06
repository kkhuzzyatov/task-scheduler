package task_scheduler.task_tracker_scheduler.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.dto.ReportRequest;

@Component
@RequiredArgsConstructor
public class ReportRequestProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(ReportRequest request) {

    System.out.println("Sending report request: " + request);

    kafkaTemplate.send("report-requests", request.getUserEmail(), request);
  }
}
