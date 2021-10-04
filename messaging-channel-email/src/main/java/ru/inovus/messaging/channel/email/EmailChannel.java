package ru.inovus.messaging.channel.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация канала отправки уведомлений по Email
 */
@Slf4j
@PropertySource("classpath:channel.properties")
@Component
public class EmailChannel extends AbstractChannel {

    private JavaMailSender emailSender;

    public EmailChannel(@Value("${novus.messaging.channel.email.queue}") String messageQueueName,
                        @Value("${novus.messaging.queue.status}") String statusQueueName,
                        MqProvider mqProvider,
                        JavaMailSender emailSender) {
        super(mqProvider, messageQueueName, statusQueueName);
        this.emailSender = emailSender;
    }


    public void send(Message message) {
        log.info("Sending email " + message);
        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setMessageId(message.getId());
        messageStatus.setSystemId(message.getTenantCode());

        List<String> recipientsEmailList = message.getRecipients().stream()
                .map(Recipient::getUsername)
                .filter(email -> !StringUtils.isEmpty(email))
                .collect(Collectors.toList());
        if (!recipientsEmailList.isEmpty()) {
            try {
                MimeMessage mail = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(recipientsEmailList.toArray(String[]::new));
                helper.setSubject(message.getCaption());
                helper.setText(message.getText(), true);

                emailSender.send(mail);
                messageStatus.setStatus(MessageStatusType.SENT);
                sendStatus(messageStatus);
            } catch (Exception e) {
                log.error("MimeMessage create and send email failed! {}", e.getMessage());
                messageStatus.setStatus(MessageStatusType.FAILED);
                messageStatus.setErrorMessage(e.getMessage());
                sendStatus(messageStatus);
            }
        } else {
            messageStatus.setStatus(MessageStatusType.FAILED);
            messageStatus.setErrorMessage("There are no recipient's email addresses to send");
            sendStatus(messageStatus);
        }
    }
}
