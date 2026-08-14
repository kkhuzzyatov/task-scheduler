package task_scheduler.task_tracker_email_sender.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "unisender")
public record UnisenderProperties(String baseUrl, String apiKey) {}
