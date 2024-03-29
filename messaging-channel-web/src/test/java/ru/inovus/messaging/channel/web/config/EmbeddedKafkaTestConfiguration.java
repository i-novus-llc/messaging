package ru.inovus.messaging.channel.web.config;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import ru.inovus.messaging.mq.support.kafka.ObjectSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EmbeddedKafkaTestConfiguration {

    @Bean
    @ConditionalOnBean(EmbeddedKafkaBroker.class)
    public KafkaTemplate<?, ?> kafkaTemplate(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        ProducerFactory<?, ?> producer = new DefaultKafkaProducerFactory<>(
                configs, new StringSerializer(), new ObjectSerializer());
        return new KafkaTemplate(producer);
    }

    @Bean
    @ConditionalOnBean(EmbeddedKafkaBroker.class)
    public KafkaProperties kafkaProperties(@Value("${spring.embedded.kafka.brokers}") String broker) {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.getConsumer().setBootstrapServers(Collections.singletonList(broker));
        return kafkaProperties;
    }
}
