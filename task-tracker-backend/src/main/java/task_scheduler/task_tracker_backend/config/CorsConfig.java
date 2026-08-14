package task_scheduler.task_tracker_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import task_scheduler.task_tracker_backend.properties.FrontendProperties;

@RequiredArgsConstructor
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  private final FrontendProperties frontendProperties;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/api/**")
        .allowedOrigins(frontendProperties.url())
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        .allowedHeaders("*");
  }
}
