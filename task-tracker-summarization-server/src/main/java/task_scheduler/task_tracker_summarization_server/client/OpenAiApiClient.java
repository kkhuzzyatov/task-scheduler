package task_scheduler.task_tracker_summarization_server.client;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;
import task_scheduler.task_tracker_summarization_server.properties.OpenAiProperties;

@RequiredArgsConstructor
public class OpenAiApiClient {

  private final RestClient restClient;
  private final OpenAiProperties properties;

  public String generateSummary(String prompt) {

    OpenAiResponse response =
        restClient
            .post()
            .uri(properties.baseUrl() + "/chat/completions")
            .header("Authorization", "Bearer " + properties.apiKey())
            .header("Content-Type", "application/json")
            .body(
                Map.of(
                    "model", properties.model(),
                    "messages", List.of(Map.of("role", "user", "content", prompt))))
            .retrieve()
            .body(OpenAiResponse.class);

    if (response == null || response.choices() == null || response.choices().isEmpty()) {
      throw new IllegalStateException("OpenAI response contains no choices");
    }

    return response.choices().get(0).message().content();
  }

  public record OpenAiResponse(List<Choice> choices) {}

  public record Choice(Message message) {}

  public record Message(String content) {}
}
