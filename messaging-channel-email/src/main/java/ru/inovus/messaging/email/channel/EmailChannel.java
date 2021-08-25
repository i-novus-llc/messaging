package ru.inovus.messaging.email.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.channel.api.queue.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:channel.properties")
public class EmailChannel extends AbstractChannel {

    private static final Logger logger = LoggerFactory.getLogger(EmailChannel.class);

    private JavaMailSender emailSender;

    public EmailChannel(@Value("${messaging.channel.email-queue-name}") String queueName, MqProvider mqProvider, JavaMailSender emailSender) {
        super(queueName, mqProvider);
        this.emailSender = emailSender;
    }

    /**
     * Отправка сообщения на почту
     */
    public void send(MessageOutbox message) {
        try {
            for (Recipient recipient : message.getMessage().getRecipients()) {
                if (StringUtils.isEmpty(recipient.getEmail())) {
                    logger.error("Message with id={} will not be sent to recipient with id={} due to an empty email address", message.getMessage().getId(), recipient.getRecipient());
                }
            }
            List<String> recipientsEmailList = message.getMessage().getRecipients().stream()
                    .filter(x -> StringUtils.hasLength(x.getEmail()))
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

            //todo  тут должна быть очередь для статусов
            reportSendStatus();
//            messageService.setSendEmailResult(UUID.fromString(message.getMessage().getId()), LocalDateTime.now(), null);
        } catch (Exception e) {
            logger.error("MimeMessage create and send email failed! {}", e.getMessage(), e);
            //todo  тут должна быть очередь для статусов
            reportSendStatus();
//            messageService.setSendEmailResult(UUID.fromString(message.getMessage().getId()), LocalDateTime.now(), ExceptionUtils.getMessage(e) + "." + ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reportSendStatus() {

    }
}
