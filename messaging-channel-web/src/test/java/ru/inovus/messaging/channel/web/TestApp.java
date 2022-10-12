package ru.inovus.messaging.channel.web;

import net.n2oapp.platform.jaxrs.autoconfigure.JaxRsServerAutoConfiguration;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.inovus.messaging.channel.web.config.EmbeddedKafkaTestConfiguration;
import ru.inovus.messaging.channel.web.config.WebSecurityTestConfiguration;
import ru.inovus.messaging.channel.web.configuration.EnableWebChannel;

@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class,
        KafkaAutoConfiguration.class,
        JaxRsServerAutoConfiguration.class,
        CxfAutoConfiguration.class})
@Import({WebSecurityTestConfiguration.class, EmbeddedKafkaTestConfiguration.class})
@EnableWebSecurity
@EnableWebChannel
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
