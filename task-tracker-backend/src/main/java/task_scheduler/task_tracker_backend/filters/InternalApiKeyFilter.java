package task_scheduler.task_tracker_backend.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import task_scheduler.task_tracker_backend.properties.InternalApiProperties;
import task_scheduler.task_tracker_backend.properties.LogProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-Internal-Api-Key";
  private static final String CALLER_SERVICE_HEADER = "X-Caller-Service";

  private final InternalApiProperties properties;
  private final LogProperties logProperties;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!request.getRequestURI().startsWith("/internal/")) {
      filterChain.doFilter(request, response);
      return;
    }

    String apiKey = request.getHeader(API_KEY_HEADER);
    String callerService = request.getHeader(CALLER_SERVICE_HEADER);

    if (apiKey == null || !apiKey.equals(properties.apiKey())) {

      log.atWarn()
          .addKeyValue("service", logProperties.name())
          .addKeyValue("event", "internal_api_auth_failed")
          .addKeyValue("callerService", callerService)
          .log("Internal API unauthorized");

      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write("{\"status\":401,\"message\":\"Invalid internal API key\"}");

      return;
    }

    filterChain.doFilter(request, response);
  }
}
