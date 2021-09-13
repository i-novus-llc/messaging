package ru.inovus.messaging.email.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.model.enums.SendStatus;
import ru.inovus.messaging.channel.api.queue.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.models.MessageQO;
import ru.inovus.messaging.channel.api.queue.models.MessageStatusQO;

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

    public EmailChannel(@Value("${novus.messaging.channel.email.queue}") String messageQueueName,
                        @Value("${novus.messaging.status.queue}") String statusQueueName,
                        MqProvider mqProvider,
                        JavaMailSender emailSender) {
        super(mqProvider, messageQueueName, statusQueueName);
        this.emailSender = emailSender;
    }


    public void send(MessageQO message) {
        MessageStatusQO statusQO = new MessageStatusQO();
        statusQO.setMessageId(message.getId());

        try {
            for (MessageQO.RecipientQO recipient : message.getRecipients()) {
                if (StringUtils.isEmpty(recipient.getEmail()))
                    log.error("Message with id={} will not be sent to recipient with id={} due to an empty email address",
                            message.getId(), recipient.getName());
            }

            List<String> recipientsEmailList = message.getRecipients().stream()
                    .filter(x -> !StringUtils.isEmpty(x.getEmail()))
                    .map(MessageQO.RecipientQO::getEmail)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(recipientsEmailList)) {
                MimeMessage mail = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true);
                helper.setTo(recipientsEmailList.toArray(String[]::new));
                helper.setSubject(message.getCaption());
                helper.setText(message.getText(), true);
                emailSender.send(mail);
            }
            statusQO.setStatus(SendStatus.SENT);
        } catch (Exception e) {
            log.error("MimeMessage create and send email failed! {}", e.getMessage());
            statusQO.setStatus(SendStatus.FAILED);
            statusQO.setErrorMessage(e.getMessage());
        } finally {
            super.reportSendStatus(statusQO);
        }
    }
}
