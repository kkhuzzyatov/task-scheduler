package task_scheduler.task_tracker_summarization_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import task_scheduler.task_tracker_summarization_server.client.OpenAiApiClient;
import task_scheduler.task_tracker_summarization_server.properties.OpenAiProperties;

@Configuration
public class OpenAiClientConfig {

  @Bean
  public OpenAiApiClient openAiApiClient(RestClient restClient, OpenAiProperties properties) {
    return new OpenAiApiClient(restClient, properties);
  }
}
