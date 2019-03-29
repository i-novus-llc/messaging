package ru.inovus.messaging.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.stream.Collectors;

@Component
public class EmailSender {

    @Autowired
    private JavaMailSender emailSender;

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
            //Отправка уведомления о доставке
//        mail.addHeader("Disposition-Notification-To", "http://some_rest_address");
            emailSender.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
