package ru.inovus.messaging.email.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.SendStatus;
import ru.inovus.messaging.channel.api.queue.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация канала отправки сообщений по Email
 */
@Slf4j
@PropertySource("classpath:channel.properties")
@Component
public class EmailChannel extends AbstractChannel {

    private JavaMailSender emailSender;

    public EmailChannel(@Value("${messaging.channel.email-queue-name}") String queueName,
                        MqProvider mqProvider,
                        JavaMailSender emailSender) {
        super(queueName, mqProvider);
        this.emailSender = emailSender;
    }


    public void send(MessageOutbox message) {
        try {
            for (Recipient recipient : message.getMessage().getRecipients()) {
                if (StringUtils.isEmpty(recipient.getEmail()))
                    log.error("Message with id={} will not be sent to recipient with id={} due to an empty email address",
                            message.getMessage().getId(), recipient.getName());
            }

            List<String> recipientsEmailList = message.getMessage().getRecipients().stream()
                    .filter(x -> !StringUtils.isEmpty(x.getEmail()))
                    .map(Recipient::getEmail)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(recipientsEmailList)) {
                MimeMessage mail = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(recipientsEmailList.toArray(String[]::new));
                helper.setSubject(message.getMessage().getCaption());
                helper.setText(message.getMessage().getText(), true);
                emailSender.send(mail);
            }
            reportSendStatus(SendStatus.SENT);
        } catch (Exception e) {
            log.error("MimeMessage create and send email failed! {}", e.getMessage());
            reportSendStatus(SendStatus.FAILED);
        }
    }

    @Override
    public void reportSendStatus(SendStatus status) {

    }
}
