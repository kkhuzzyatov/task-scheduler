package task_scheduler.task_tracker_email_sender.storage;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TemporaryConsumedMessagesStorage {

  private final List<String> messages = new ArrayList<>();

  public void add(String message) {
    messages.add(message);
  }

  public List<String> getMessages() {
    return new ArrayList<>(messages);
  }
}
