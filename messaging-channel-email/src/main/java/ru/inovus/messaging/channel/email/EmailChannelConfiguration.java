package ru.inovus.messaging.channel.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.MessageAttachment;
import ru.inovus.messaging.channel.api.queue.MqProvider;

@Configuration
@EnableConfigurationProperties({EmailChannelProperties.class})
public class EmailChannelConfiguration {

    @Value("${novus.messaging.queue.status}")
    private String statusQueueName;
    @Value("${spring.mail.username}")
    private String senderUserName;

    @Bean
    EmailChannel emailChannel(EmailChannelProperties emailChannelProperties,
                              MqProvider mqProvider,
                              JavaMailSender emailSender,
                              @Nullable MessageAttachment attachmentService) {
        return new EmailChannel(emailChannelProperties.getQueue(), statusQueueName, mqProvider, emailSender,
                !StringUtils.hasText(emailChannelProperties.getFrom())? senderUserName : emailChannelProperties.getFrom(), attachmentService);
    }
}
