package task_scheduler.task_tracker_backend.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_backend.dto.email.WelcomeMessage;
import task_scheduler.task_tracker_backend.properties.LogProperties;
import task_scheduler.task_tracker_backend.user.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class WelcomeMessageProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  private final LogProperties logProperties;

  public void send(WelcomeMessage task, User user) {
    String topicName = "welcome-message";
    kafkaTemplate.send(topicName, task.recipient(), task);
    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "welcome_email_created")
        .addKeyValue("userId", user.getUserId())
        .addKeyValue("email", user.getEmail())
        .addKeyValue("topic", topicName)
        .log("Email notification created");
  }
}
