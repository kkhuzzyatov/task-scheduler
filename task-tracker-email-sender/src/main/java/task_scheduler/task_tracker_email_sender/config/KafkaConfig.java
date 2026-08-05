package task_scheduler.task_tracker_email_sender.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import task_scheduler.task_tracker_email_sender.dto.EmailTask;

@Configuration
public class KafkaConfig {

  @Bean
  public ConsumerFactory<String, EmailTask> consumerFactory() {

    Map<String, Object> props = new HashMap<>();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");

    props.put(ConsumerConfig.GROUP_ID_CONFIG, "email-service");

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

    props.put(
        JsonDeserializer.TYPE_MAPPINGS,
        "task_scheduler.task_tracker_backend.dto.email.EmailTask:"
            + "task_scheduler.task_tracker_email_sender.dto.EmailTask");

    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, EmailTask> kafkaListenerContainerFactory(
      ConsumerFactory<String, EmailTask> consumerFactory) {

    var factory = new ConcurrentKafkaListenerContainerFactory<String, EmailTask>();

    factory.setConsumerFactory(consumerFactory);

    return factory;
  }
}
