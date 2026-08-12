package task_scheduler.task_tracker_email_sender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import task_scheduler.task_tracker_email_sender.client.UnisenderClient;
import task_scheduler.task_tracker_email_sender.dto.ReportResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final UnisenderClient unisenderClient;

  public void sendReportEmail(ReportResponse response) {

    send(response.userEmail(), "Daily report", response.summary());
  }

  public void sendWelcomeMessage(String email) {

    send(
        email,
        "Welcome message",
        "Thanks for sign up in task tracker. Hope you'll like our product");
  }

  private void send(String email, String subject, String body) {

    unisenderClient.sendEmail(email, subject, body);
  }
}
