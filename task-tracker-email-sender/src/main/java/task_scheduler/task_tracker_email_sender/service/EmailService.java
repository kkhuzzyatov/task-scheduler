package task_scheduler.task_tracker_email_sender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_email_sender.dto.WelcomeMessage;
import task_scheduler.task_tracker_email_sender.dto.ReportResponse;
import task_scheduler.task_tracker_email_sender.properties.LogProperties;
import task_scheduler.task_tracker_email_sender.storage.TemporaryConsumedMessagesStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final TemporaryConsumedMessagesStorage storage;
  private final LogProperties logProperties;

  public void sendReportEmail(ReportResponse response) {

    send(response.userEmail(), "Daily report", response.summary(), response.userEmail());
  }

  public void sendEmail(WelcomeMessage task) {

    send(task.getRecipient(), task.getSubject(), task.getText(), task.getRecipient());
  }

  private void send(String recipient, String subject, String text, String userId) {

    long start = System.currentTimeMillis();

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "email_sending_started")
        .addKeyValue("userId", userId)
        .addKeyValue("recipient", recipient)
        .log("Email sending started");

    try {

      /*
       * Temporary implementation.
       * Replace this with real email provider call later.
       */
      storage.addMessage(recipient + ": " + subject + " - " + text);

      long durationMs = System.currentTimeMillis() - start;

      log.atInfo()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "email_sent")
          .addKeyValue("recipient", recipient)
          .addKeyValue("durationMs", durationMs)
          .log("Email sent successfully");

    } catch (Exception e) {

      log.atError()
          .setCause(e)
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "email_sending_failed")
          .addKeyValue("recipient", recipient)
          .addKeyValue("exception", e.getClass().getSimpleName())
          .log("Email sending failed");

      throw e;
    }
  }
}
