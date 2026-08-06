package task_scheduler.task_tracker_summarization_server.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_server.dto.ReportResponse;

@Component
@RequiredArgsConstructor
public class ReportResponseProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(ReportResponse response) {

    System.out.println("Sending report response: " + response);

    kafkaTemplate.send("report-response", response.userEmail(), response);
  }
}
