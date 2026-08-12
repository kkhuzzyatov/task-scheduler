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
  private final EmailTemplateService emailTemplateService;

  public void sendReportEmail(ReportResponse response) {

    String html = emailTemplateService.createReportEmail(response.summary());

    unisenderClient.sendEmail(response.userEmail(), "Daily report", html);
  }

  public void sendWelcomeMessage(String email) {

    String html = emailTemplateService.createWelcomeEmail();

    unisenderClient.sendEmail(email, "Welcome message", html);
  }
}
