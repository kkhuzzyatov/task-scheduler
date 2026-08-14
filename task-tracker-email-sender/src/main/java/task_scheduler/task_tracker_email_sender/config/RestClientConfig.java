package task_scheduler.task_tracker_email_sender.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import task_scheduler.task_tracker_email_sender.properties.UnisenderProperties;

@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

  private final UnisenderProperties unisenderProperties;

  @Bean
  public RestClient unisenderRestClient(UnisenderProperties properties) {

    ClientHttpRequestInterceptor apiKeyInterceptor =
        (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
          URI newUri =
              UriComponentsBuilder.fromUri(request.getURI())
                  .queryParam("api_key", properties.apiKey())
                  .build(true)
                  .toUri();

          HttpRequest newRequest =
              HttpRequestWrapper.builder().request(request).uri(newUri).build();

          return execution.execute(newRequest, body);
        };

    return RestClient.builder()
        .baseUrl(properties.baseUrl())
        .requestInterceptor(apiKeyInterceptor)
        .build();
  }
}
