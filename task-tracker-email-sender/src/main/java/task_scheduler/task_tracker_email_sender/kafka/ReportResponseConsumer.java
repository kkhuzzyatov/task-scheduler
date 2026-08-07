package task_scheduler.task_tracker_email_sender.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_email_sender.dto.ReportResponse;
import task_scheduler.task_tracker_email_sender.properties.LogProperties;
import task_scheduler.task_tracker_email_sender.service.EmailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportResponseConsumer {

  private final EmailService emailService;
  private final LogProperties logProperties;

  @KafkaListener(
      topics = "report-response",
      containerFactory = "reportKafkaListenerContainerFactory")
  public void consume(ReportResponse response) {

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "report_response_received")
        .addKeyValue("userId", response.userEmail())
        .log("Report response received from Kafka");

    emailService.processReport(response);
  }
}
