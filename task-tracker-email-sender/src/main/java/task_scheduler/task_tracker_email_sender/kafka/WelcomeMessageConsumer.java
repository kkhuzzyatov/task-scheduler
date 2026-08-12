package task_scheduler.task_tracker_email_sender.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_email_sender.dto.WelcomeMessage;
import task_scheduler.task_tracker_email_sender.properties.LogProperties;
import task_scheduler.task_tracker_email_sender.service.EmailService;

@Component
@RequiredArgsConstructor
@Slf4j
public class WelcomeMessageConsumer {

  private final EmailService emailService;
  private final LogProperties logProperties;

  @KafkaListener(topics = "welcome-message", containerFactory = "emailKafkaListenerContainerFactory")
  public void consume(WelcomeMessage task) {

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "email_task_received")
        .addKeyValue("recipient", task.getRecipient())
        .log("Email task received from Kafka");

    emailService.sendEmail(task);
  }
}
