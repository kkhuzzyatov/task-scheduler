package task_scheduler.task_tracker_summarization_server.client;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import task_scheduler.task_tracker_summarization_server.properties.LogProperties;
import task_scheduler.task_tracker_summarization_server.properties.OpenAiProperties;

@Slf4j
@RequiredArgsConstructor
public class OpenAiApiClient {

  private static final String LOG_KEY_SERVICE = "service";
  private static final String LOG_KEY_EVENT = "event";
  private static final String LOG_KEY_USER_ID = "userId";

  private final RestClient restClient;
  private final OpenAiProperties properties;
  private final LogProperties logProperties;

  public String generateSummary(String prompt, String userId) {

    long start = System.currentTimeMillis();

    log.atDebug()
        .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
        .addKeyValue(LOG_KEY_EVENT, "openai_request_started")
        .addKeyValue("model", properties.model())
        .addKeyValue(LOG_KEY_USER_ID, userId)
        .log("OpenAI request started");

    try {

      OpenAiResponse response =
          restClient
              .post()
              .uri(properties.baseUrl() + "/chat/completions")
              .header("Authorization", "Bearer " + properties.apiKey())
              .header("Content-Type", "application/json")
              .body(
                  Map.of(
                      "model",
                      properties.model(),
                      "messages",
                      List.of(Map.of("role", "user", "content", prompt))))
              .retrieve()
              .body(OpenAiResponse.class);

      if (response == null || response.choices() == null || response.choices().isEmpty()) {
        throw new IllegalStateException("OpenAI response contains no choices");
      }

      long duration = System.currentTimeMillis() - start;

      log.atInfo()
          .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
          .addKeyValue(LOG_KEY_EVENT, "report_summary_generated")
          .addKeyValue(LOG_KEY_USER_ID, userId)
          .addKeyValue("durationMs", duration)
          .log("OpenAI response received");

      return response.choices().get(0).message().content();

    } catch (HttpStatusCodeException e) {

      log.atError()
          .setCause(e)
          .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
          .addKeyValue(LOG_KEY_EVENT, "openai_request_failed")
          .addKeyValue(LOG_KEY_USER_ID, userId)
          .addKeyValue("statusCode", e.getStatusCode().value())
          .addKeyValue("exception", e.getClass().getSimpleName())
          .log("OpenAI request failed");

      throw e;

    } catch (Exception e) {

      log.atError()
          .setCause(e)
          .addKeyValue(LOG_KEY_SERVICE, logProperties.name())
          .addKeyValue(LOG_KEY_EVENT, "openai_request_failed")
          .addKeyValue(LOG_KEY_USER_ID, userId)
          .addKeyValue("exception", e.getClass().getSimpleName())
          .log("OpenAI request failed");

      throw e;
    }
  }

  public record OpenAiResponse(List<Choice> choices) {}

  public record Choice(Message message) {}

  public record Message(String content) {}
}
