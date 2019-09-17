package ru.inovus.messaging.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.queue.QueueMqConsumer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private JavaMailSender emailSender;

    public EmailSender(JavaMailSender emailSender, MqProvider mqProvider, @Value("${novus.messaging.topic.email}") String emailQueueName) {
        this.emailSender = emailSender;
        mqProvider.subscribe(new QueueMqConsumer(emailQueueName, this::send, emailQueueName));
    }

    /**
     * Отправка сообщения на почту
     */
    public void send(MessageOutbox message) {
        for (Recipient recipient : message.getMessage().getRecipients()) {
            if (StringUtils.isEmpty(recipient.getEmail())) {
                logger.error("Message with id={} will not be sent to recipient with id={} due to an empty email address", message.getMessage().getId(), recipient.getRecipient());
            }
        }
        List<String> recipientsEmailList = message.getMessage().getRecipients().stream()
            .filter(x -> StringUtils.isNotEmpty(x.getEmail()))
            .map(Recipient::getEmail)
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(recipientsEmailList)) {
            try {
                MimeMessage mail = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(recipientsEmailList.toArray(new String[recipientsEmailList.size()]));
                helper.setSubject(message.getMessage().getCaption());
                helper.setText(message.getMessage().getText(), true);
                emailSender.send(mail);
            } catch (MessagingException e) {
                logger.error("MimeMessage create and send email failed!" + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }
}
