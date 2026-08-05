package task_scheduler.task_tracker_backend.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_backend.dto.email.EmailTask;

@Component
@RequiredArgsConstructor
public class EmailProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void send(EmailTask task) {

    System.out.println("Sending email task: " + task);

    kafkaTemplate.send("email-tasks", task.recipient(), task);
  }
}
