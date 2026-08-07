package task_scheduler.task_tracker_summarization_server.config;

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
import task_scheduler.task_tracker_summarization_server.dto.ReportRequest;
import task_scheduler.task_tracker_summarization_server.properties.KafkaProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;


    private Map<String, Object> consumerProps() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.bootstrapServers()
        );

        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "report-requests"
        );

        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        return props;
    }


    @Bean
    public ConsumerFactory<String, ReportRequest> reportRequestConsumerFactory() {

        JsonDeserializer<ReportRequest> deserializer =
                new JsonDeserializer<>(ReportRequest.class, false);

        deserializer.addTrustedPackages(
                "task_scheduler.task_tracker_summarization_server.dto"
        );

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReportRequest>
    reportRequestKafkaListenerContainerFactory() {

        var factory =
                new ConcurrentKafkaListenerContainerFactory<String, ReportRequest>();

        factory.setConsumerFactory(
                reportRequestConsumerFactory()
        );

        return factory;
    }
}