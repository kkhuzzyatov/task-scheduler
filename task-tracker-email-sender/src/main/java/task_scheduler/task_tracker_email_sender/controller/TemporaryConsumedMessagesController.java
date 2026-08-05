package task_scheduler.task_tracker_email_sender.controller;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task_scheduler.task_tracker_email_sender.storage.TemporaryConsumedMessagesStorage;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class TemporaryConsumedMessagesController {

  private final TemporaryConsumedMessagesStorage storage;

  @GetMapping
  public Map<String, List<String>> getMessages() {
    return Map.of("messages", storage.getMessages());
  }
}
