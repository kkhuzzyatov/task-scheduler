package task_scheduler.task_tracker_scheduler.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "internal")
public record InternalApiProperties(String apiKey) {}
