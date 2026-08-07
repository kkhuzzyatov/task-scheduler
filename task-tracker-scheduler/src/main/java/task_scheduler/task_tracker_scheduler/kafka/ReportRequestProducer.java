package task_scheduler.task_tracker_scheduler.kafka;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_scheduler.dto.ReportRequest;
import task_scheduler.task_tracker_scheduler.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportRequestProducer {

  private static final String TOPIC = "report-requests";

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final LogProperties logProperties;

  public void send(ReportRequest request) {

    String messageId = UUID.randomUUID().toString();

    kafkaTemplate
        .send(TOPIC, request.getUserEmail(), request)
        .whenComplete(
            (result, exception) -> {
              if (exception != null) {
                log.atError()
                    .setCause(exception)
                    .addKeyValue("service", logProperties.name())
                    .addKeyValue("event", "report_request_send_failed")
                    .addKeyValue("topic", TOPIC)
                    .addKeyValue("userId", request.getUserEmail())
                    .log("Failed to send report request");

                return;
              }

              log.atInfo()
                  .addKeyValue("service", logProperties.name())
                  .addKeyValue("event", "report_request_sent_to_summarization")
                  .addKeyValue("topic", TOPIC)
                  .addKeyValue("userId", request.getUserEmail())
                  .addKeyValue("messageId", messageId)
                  .log("Send report request to summarization");
            });
  }
}
