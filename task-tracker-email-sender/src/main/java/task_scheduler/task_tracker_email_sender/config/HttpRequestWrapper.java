package task_scheduler.task_tracker_email_sender.config;

import java.net.URI;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

@AllArgsConstructor
@Builder
public class HttpRequestWrapper implements HttpRequest {

  private final HttpRequest request;
  private final URI uri;

  @Override
  public URI getURI() {
    return uri;
  }

  @Override
  public HttpMethod getMethod() {
    return request.getMethod();
  }

  @Override
  public HttpHeaders getHeaders() {
    return request.getHeaders();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return request.getAttributes();
  }
}
