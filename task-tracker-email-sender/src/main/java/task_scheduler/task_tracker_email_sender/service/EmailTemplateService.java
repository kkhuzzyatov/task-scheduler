package task_scheduler.task_tracker_email_sender.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import task_scheduler.task_tracker_email_sender.properties.AppProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {

  private final SpringTemplateEngine templateEngine;
  private final AppProperties appProperties;

  public String createReportEmail(String summary) {

    log.atInfo()
        .addKeyValue("event", "report_email_template_processing_started")
        .addKeyValue("template", "report-email")
        .addKeyValue("summaryLength", summary.length())
        .log("Processing report email template");

    Context context = new Context();

    context.setVariable("summary", summary);

    String html = templateEngine.process("report-email", context);

    log.atInfo()
        .addKeyValue("event", "report_email_template_processed")
        .addKeyValue("template", "report-email")
        .addKeyValue("htmlLength", html.length())
        .log("Report email template processed successfully");

    return html;
  }

  public String createWelcomeEmail() {

    log.atInfo()
        .addKeyValue("event", "welcome_email_template_processing_started")
        .addKeyValue("template", "welcome-email")
        .addKeyValue("appUrl", appProperties.url())
        .log("Processing welcome email template");

    Context context = new Context();

    context.setVariable("appUrl", appProperties.url());

    String html = templateEngine.process("welcome-email", context);

    log.atInfo()
        .addKeyValue("event", "welcome_email_template_processed")
        .addKeyValue("template", "welcome-email")
        .addKeyValue("htmlLength", html.length())
        .log("Welcome email template processed successfully");

    return html;
  }
}
