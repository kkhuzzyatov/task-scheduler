package task_scheduler.task_tracker_summarization_service.config;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import task_scheduler.task_tracker_summarization_service.properties.KafkaProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

  private final KafkaProperties kafkaProperties;

  private Map<String, Object> producerProps() {

    Map<String, Object> props = new HashMap<>();

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());

    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

    return props;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {

    return new DefaultKafkaProducerFactory<>(producerProps());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {

    return new KafkaTemplate<>(producerFactory());
  }
}
