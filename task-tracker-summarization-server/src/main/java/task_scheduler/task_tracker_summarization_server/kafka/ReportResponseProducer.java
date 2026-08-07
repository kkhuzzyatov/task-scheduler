package task_scheduler.task_tracker_summarization_server.kafka;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_summarization_server.dto.ReportResponse;
import task_scheduler.task_tracker_summarization_server.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportResponseProducer {

  private static final String TOPIC = "report-response";

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final LogProperties logProperties;

  public void send(ReportResponse response) {

    String messageId = UUID.randomUUID().toString();

    kafkaTemplate
        .send(TOPIC, response.userEmail(), response)
        .whenComplete(
            (result, exception) -> {
              if (exception != null) {
                log.atError()
                    .setCause(exception)
                    .addKeyValue("service", logProperties.name())
                    .addKeyValue("event", "report_response_send_failed")
                    .addKeyValue("topic", TOPIC)
                    .addKeyValue("userId", response.userEmail())
                    .log("Failed to send generated report");

                return;
              }

              log.atInfo()
                  .addKeyValue("service", logProperties.name())
                  .addKeyValue("event", "report_response_sent_to_email_sender")
                  .addKeyValue("topic", TOPIC)
                  .addKeyValue("userId", response.userEmail())
                  .addKeyValue("messageId", messageId)
                  .log("Send generated report to email sender");
            });
  }
}
