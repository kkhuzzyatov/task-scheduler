package task_scheduler.task_tracker_email_sender.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import task_scheduler.task_tracker_email_sender.dto.EmailTask;
import task_scheduler.task_tracker_email_sender.storage.TemporaryConsumedMessagesStorage;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

  private final TemporaryConsumedMessagesStorage storage;

  @KafkaListener(topics = "email-tasks", containerFactory = "emailKafkaListenerContainerFactory")
  public void consume(EmailTask task) {

    storage.addMessage(task.getRecipient() + ": " + task.getSubject() + " - " + task.getText());
  }
}
