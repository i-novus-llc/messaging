package ru.inovus.messaging.email.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatus;
import ru.inovus.messaging.channel.api.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.model.QueueMessageStatus;

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
                        @Value("${novus.messaging.status.queue}") String statusQueueName,
                        MqProvider mqProvider,
                        JavaMailSender emailSender) {
        super(mqProvider, messageQueueName, statusQueueName);
        this.emailSender = emailSender;
    }


    public void send(Message message) {
        QueueMessageStatus messageStatus = new QueueMessageStatus();
        messageStatus.setId(message.getId());

        try {
            for (Recipient recipient : message.getRecipients()) {
                if (StringUtils.isEmpty(recipient.getRecipientSendChannelId()))
                    log.error("Message with id={} will not be sent to recipient with id={} due to an empty email address",
                            message.getId(), recipient.getRecipientSendChannelId());
            }

            List<String> recipientsEmailList = message.getRecipients().stream()
                    .map(Recipient::getRecipientSendChannelId)
                    .filter(email -> !StringUtils.isEmpty(email))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(recipientsEmailList)) {
                MimeMessage mail = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(recipientsEmailList.toArray(String[]::new));
                helper.setSubject(message.getCaption());
                helper.setText(message.getText(), true);
                emailSender.send(mail);
            }
            messageStatus.setStatus(MessageStatus.SENT);
            sendStatus(messageStatus);
        } catch (Exception e) {
            log.error("MimeMessage create and send email failed! {}", e.getMessage());
            messageStatus.setStatus(MessageStatus.FAILED);
            messageStatus.setSendErrorMessage(e.getMessage());
            sendStatus(messageStatus);
        }
    }
}
