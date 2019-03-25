package ru.inovus.messaging.server.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.MqProvider;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.rest.MessageRest;
import ru.inovus.messaging.impl.MessageService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Controller
@Slf4j
public class MessageRestImpl implements MessageRest {

    private final JavaMailSender emailSender;
    private final MessageService messageService;
    private final Long timeout;
    private final MqProvider mqProvider;

    public MessageRestImpl(MessageService messageService,
                           @Value("${novus.messaging.timeout}") Long timeout,
                           MqProvider mqProvider,
                           JavaMailSender emailSender) {
        this.messageService = messageService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
        this.emailSender = emailSender;
    }

    @Override
    public Page<Message> getMessages(MessageCriteria criteria) {
        return messageService.getMessages(criteria);
    }

    @Override
    public Message getMessage(String id) {
        return messageService.getMessage(id);
    }

    @Override
    public void sendMessage(final MessageOutbox message) {
        if (message.getMessage() != null) {
            Recipient[] init = new Recipient[0];
            Recipient[] recipients = message.getMessage().getRecipients() != null ?
                message.getMessage().getRecipients().toArray(init) : init;
            Message msg = messageService.createMessage(message.getMessage(), recipients);
            message.getMessage().setId(msg.getId());

            if (!message.getMessage().getInfoType().equals(InfoType.NOTICE)) {
                try {
                    sendEmail(message);
                } catch (MessagingException e) {
                    log.debug(e.getMessage());
                }
            }
        }
        mqProvider.publish(message);
    }

    public void markRead(String recipient, String id) {
        messageService.markRead(recipient, id);
    }

    /**
     * Отправка сообщений на почту
     *
     * @param message сообщение
     */
    public void sendEmail(MessageOutbox message) throws MessagingException {
        MimeMessage mail = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setTo(message.getMessage().getRecipients().get(0).getEmail());
        helper.setSubject(message.getMessage().getCaption());
        helper.setText(message.getMessage().getText(), true);
        //Отправка уведомления о доставке
//        mail.addHeader("Disposition-Notification-To","azainutdinov@i-novus.ru");
        emailSender.send(mail);
    }
}
