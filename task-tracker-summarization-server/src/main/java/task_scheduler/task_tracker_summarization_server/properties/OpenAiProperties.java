package task_scheduler.task_tracker_summarization_server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(String baseUrl, String apiKey, String model) {}
