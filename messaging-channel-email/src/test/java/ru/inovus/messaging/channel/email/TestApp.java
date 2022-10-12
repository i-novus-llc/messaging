package ru.inovus.messaging.channel.email;

import net.n2oapp.platform.jaxrs.autoconfigure.JaxRsServerAutoConfiguration;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootApplication(exclude = {
        UserDetailsServiceAutoConfiguration.class,
        KafkaAutoConfiguration.class,
        JaxRsServerAutoConfiguration.class,
        CxfAutoConfiguration.class
})
@Import({EmbeddedKafkaTestConfiguration.class})
@EmbeddedKafka
@EnableEmailChannel
public class TestApp {
}
