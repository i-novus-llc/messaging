package ru.inovus.messaging.channel.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import ru.inovus.messaging.channel.api.queue.MqProvider;

@Configuration
@EnableConfigurationProperties({EmailChannelProperties.class})
public class EmailChannelConfiguration {

    @Value("${novus.messaging.queue.status}")
    private String statusQueueName;

    @Bean
    EmailChannel emailChannel(EmailChannelProperties emailChannelProperties,
                              MqProvider mqProvider,
                              JavaMailSender emailSender) {
        return new EmailChannel(emailChannelProperties.getQueue(), statusQueueName, mqProvider, emailSender);
    }
}
