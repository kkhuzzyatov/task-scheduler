package task_scheduler.task_tracker_email_sender.config;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;

@AllArgsConstructor
@Builder
public abstract class HttpRequestWrapper implements HttpRequest {

  private final HttpRequest request;
  private final URI uri;

  @Override
  public URI getURI() {
    return uri;
  }

  @Override
  public org.springframework.http.HttpMethod getMethod() {
    return request.getMethod();
  }

  @Override
  public HttpHeaders getHeaders() {
    return request.getHeaders();
  }
}
