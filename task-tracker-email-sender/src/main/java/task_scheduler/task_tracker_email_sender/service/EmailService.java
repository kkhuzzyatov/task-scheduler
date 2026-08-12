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

    send(response.userEmail(), "Daily report", response.summary());
  }

  public void sendWelcomeMessage(WelcomeMessage welcomeMessage) {

    send(welcomeMessage.email(), "Welcome message", "Thanks for sign up in task tracker. Hope you'll like our product");
  }

  private void send(String email, String subject, String body) {

    long start = System.currentTimeMillis();

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "email_sending_started")
        .addKeyValue("email", email)
        .addKeyValue("email subject", subject)
        .log("Email sending started");

    try {

      /*
       * Temporary implementation.
       * Replace this with real email provider call later.
       */

      long durationMs = System.currentTimeMillis() - start;

      // TODO

      log.atInfo()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "email_sent")
          .addKeyValue("email", email)
              .addKeyValue("email subject", subject)
              .addKeyValue("duration in ms", durationMs)
          .log("Email sent successfully");

    } catch (Exception e) {

      log.atError()
          .setCause(e)
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "email_sending_failed")
              .addKeyValue("email", email)
              .addKeyValue("email subject", subject)
          .addKeyValue("exception", e.getClass().getSimpleName())
          .log("Email sending failed");

      throw e;
    }
  }
}
