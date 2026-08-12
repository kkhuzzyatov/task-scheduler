package task_scheduler.task_tracker_backend.kafka;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_backend.dto.report.ReportRequest;
import task_scheduler.task_tracker_backend.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportRequestProducer {

  private static final String TOPIC = "report-requests";
  private static final String MESSAGE_ID_HEADER = "messageId";

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final LogProperties logProperties;

  public void send(ReportRequest request) {

    String messageId = UUID.randomUUID().toString();

    Message<ReportRequest> message =
        MessageBuilder.withPayload(request)
            .setHeader(MESSAGE_ID_HEADER, messageId.getBytes(StandardCharsets.UTF_8))
            .build();

    kafkaTemplate
        .send(TOPIC, request.getUserEmail(), message)
        .whenComplete(
            (SendResult<String, Object> result, Throwable exception) -> {
              if (exception != null) {
                log.atError()
                    .setCause(exception)
                    .addKeyValue("service", logProperties.name())
                    .addKeyValue("event", "report_request_send_failed")
                    .addKeyValue("topic", TOPIC)
                    .addKeyValue("userEmail", request.getUserEmail())
                    .addKeyValue("messageId", messageId)
                    .log("Failed to send report request");

                return;
              }

              log.atInfo()
                  .addKeyValue("service", logProperties.name())
                  .addKeyValue("event", "report_request_sent_to_summarization")
                  .addKeyValue("topic", TOPIC)
                  .addKeyValue("userEmail", request.getUserEmail())
                  .addKeyValue("messageId", messageId)
                  .log("Report request sent to summarization service");
            });
  }
}
