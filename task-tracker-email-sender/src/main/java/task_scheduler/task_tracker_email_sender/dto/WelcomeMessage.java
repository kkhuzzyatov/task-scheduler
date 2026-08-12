package task_scheduler.task_tracker_email_sender.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WelcomeMessage {

  private String recipient;
  private String subject;
  private String text;
}
