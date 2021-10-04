package ru.inovus.messaging.channel.email;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootApplication(exclude = {
        UserDetailsServiceAutoConfiguration.class,
        KafkaAutoConfiguration.class})
@Import({EmbeddedKafkaTestConfiguration.class})
@EmbeddedKafka
@EnableEmailChannel
public class TestApp {
}
