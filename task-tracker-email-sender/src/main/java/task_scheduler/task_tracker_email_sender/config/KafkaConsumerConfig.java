package task_scheduler.task_tracker_email_sender.config;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import task_scheduler.task_tracker_email_sender.dto.WelcomeMessage;
import task_scheduler.task_tracker_email_sender.dto.ReportResponse;
import task_scheduler.task_tracker_email_sender.properties.KafkaProperties;

@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;

  private Map<String, Object> commonProps(String groupId) {

    Map<String, Object> props = new HashMap<>();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());

    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return props;
  }

  @Bean
  public ConsumerFactory<String, WelcomeMessage> emailConsumerFactory() {

    JsonDeserializer<WelcomeMessage> deserializer = new JsonDeserializer<>(WelcomeMessage.class, false);

    deserializer.addTrustedPackages("*");

    return new DefaultKafkaConsumerFactory<>(
        commonProps("welcome-message-consumer"), new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, WelcomeMessage>
      emailKafkaListenerContainerFactory() {

    var factory = new ConcurrentKafkaListenerContainerFactory<String, WelcomeMessage>();

    factory.setConsumerFactory(emailConsumerFactory());

    return factory;
  }

  @Bean
  public ConsumerFactory<String, ReportResponse> reportConsumerFactory() {

    JsonDeserializer<ReportResponse> deserializer =
        new JsonDeserializer<>(ReportResponse.class, false);

    deserializer.addTrustedPackages("*");

    return new DefaultKafkaConsumerFactory<>(
        commonProps("report-response-consumer"), new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ReportResponse>
      reportKafkaListenerContainerFactory() {

    var factory = new ConcurrentKafkaListenerContainerFactory<String, ReportResponse>();

    factory.setConsumerFactory(reportConsumerFactory());

    return factory;
  }
}
