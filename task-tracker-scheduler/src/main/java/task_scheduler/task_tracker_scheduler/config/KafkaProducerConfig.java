package task_scheduler.task_tracker_scheduler.config;

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
import task_scheduler.task_tracker_scheduler.properties.KafkaProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;


    private Map<String, Object> jsonProducerProps() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.bootstrapServers()
        );

        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        props.put(
                JsonSerializer.ADD_TYPE_INFO_HEADERS,
                false
        );

        return props;
    }


    @Bean
    public ProducerFactory<String, Object> jsonProducerFactory() {

        return new DefaultKafkaProducerFactory<>(
                jsonProducerProps()
        );
    }


    @Bean
    public KafkaTemplate<String, Object> jsonKafkaTemplate() {

        return new KafkaTemplate<>(
                jsonProducerFactory()
        );
    }
}