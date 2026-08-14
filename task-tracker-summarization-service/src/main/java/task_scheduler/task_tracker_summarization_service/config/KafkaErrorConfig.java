package task_scheduler.task_tracker_summarization_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import task_scheduler.task_tracker_summarization_service.properties.LogProperties;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class KafkaErrorConfig {

  private final LogProperties logProperties;

  @Bean
  public DefaultErrorHandler errorHandler() {

    return new DefaultErrorHandler(
        (record, exception) -> {
          log.atError()
              .setCause(exception)
              .addKeyValue("service", logProperties.name())
              .addKeyValue("event", "kafka_message_processing_failed")
              .addKeyValue("topic", record.topic())
              .addKeyValue("partition", record.partition())
              .addKeyValue("offset", record.offset())
              .addKeyValue("messageKey", record.key())
              .addKeyValue("exception", exception.getClass().getSimpleName())
              .log("Kafka message processing failed");
        },
        new FixedBackOff(0L, 0L));
  }
}
