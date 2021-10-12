package ru.inovus.messaging.channel.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import ru.inovus.messaging.channel.web.config.EmbeddedKafkaTestConfiguration;
import ru.inovus.messaging.channel.web.config.WebSecurityTestConfiguration;
import ru.inovus.messaging.channel.web.configuration.EnableWebChannel;
import ru.inovus.messaging.channel.web.configuration.WebSocketConfiguration;
import ru.inovus.messaging.mq.support.kafka.KafkaConfig;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        KafkaAutoConfiguration.class})
@Import({WebSecurityTestConfiguration.class, EmbeddedKafkaTestConfiguration.class})
@EnableWebSecurity
@EnableWebChannel
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
