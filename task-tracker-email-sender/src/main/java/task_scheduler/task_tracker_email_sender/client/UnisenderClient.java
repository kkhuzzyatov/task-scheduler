package task_scheduler.task_tracker_email_sender.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import task_scheduler.task_tracker_email_sender.properties.LogProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnisenderClient {

  private final RestClient unisenderRestClient;
  private final LogProperties logProperties;

  public String sendEmail(String email, String subject, String body) {

    long start = System.currentTimeMillis();

    log.atInfo()
        .addKeyValue("service", logProperties.name())
        .addKeyValue("event", "email_sending_started")
        .addKeyValue("email", email)
        .addKeyValue("email subject", subject)
        .log("Email sending started");

    try {
      MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

      formData.add("email", email);
      formData.add("sender_name", "undefined");
      formData.add("sender_email", "undefined@mirorostore.ru");
      formData.add("subject", subject);
      formData.add("body", body);
      formData.add("error_checking", "1");
      formData.add("list_id", "1");

      String response =
          unisenderRestClient
              .post()
              .uri(uriBuilder -> uriBuilder.path("/sendEmail").queryParam("format", "json").build())
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .body(formData)
              .retrieve()
              .body(String.class);

      long durationMs = System.currentTimeMillis() - start;

      log.atInfo()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "email_sent")
          .addKeyValue("email", email)
          .addKeyValue("email subject", subject)
          .addKeyValue("duration in ms", durationMs)
          .log("Email sent successfully");

      return response;

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
