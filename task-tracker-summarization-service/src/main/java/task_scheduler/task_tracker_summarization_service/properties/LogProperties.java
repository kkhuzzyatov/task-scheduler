package task_scheduler.task_tracker_summarization_service.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.application")
public record LogProperties(String name) {}
