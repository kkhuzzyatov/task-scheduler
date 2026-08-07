package task_scheduler.task_tracker_summarization_server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import task_scheduler.task_tracker_summarization_server.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_server.properties.LogProperties;
import task_scheduler.task_tracker_summarization_server.properties.OpenAiProperties;

@RequiredArgsConstructor
@Configuration
public class OpenAiClientConfig {
  private final LogProperties logProperties;

  @Bean
  public OpenAiApiClient openAiApiClient(RestClient restClient, OpenAiProperties properties) {
    return new OpenAiApiClient(restClient, properties, logProperties);
  }
}
