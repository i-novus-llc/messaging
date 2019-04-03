package ru.inovus.messaging.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.queue.QueueMqConsumer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.stream.Collectors;

@Component
public class EmailSender {
    private JavaMailSender emailSender;

    public EmailSender(JavaMailSender emailSender, MqProvider mqProvider, @Value("${novus.messaging.topic.email}") String emailQueueName) {
        this.emailSender = emailSender;
        mqProvider.subscribe(new QueueMqConsumer(emailQueueName, this::send, emailQueueName));
    }

    /**
     * Отправка сообщения на почту
     *
     * @param message
     * @throws MessagingException
     */
    public void send(MessageOutbox message) {
        try {
            MimeMessage mail = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(message.getMessage().getRecipients().stream().map(Recipient::getEmail).collect(Collectors.toList())
                .toArray(new String[message.getMessage().getRecipients().size()]));
            helper.setSubject(message.getMessage().getCaption());
            helper.setText(message.getMessage().getText(), true);
            emailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
