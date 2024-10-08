package ru.inovus.messaging.channel.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.inovus.messaging.api.MessageAttachment;
import ru.inovus.messaging.api.model.AttachmentResponse;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.AbstractChannel;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Реализация канала отправки уведомлений по Email
 */
@Slf4j
public class EmailChannel extends AbstractChannel {

    private final JavaMailSender emailSender;
    private final String senderUserName;
    private final MessageAttachment attachmentService;

    public EmailChannel(String messageQueueName,
                        String statusQueueName,
                        MqProvider mqProvider,
                        JavaMailSender emailSender,
                        String senderUserName,
                        MessageAttachment attachmentService) {
        super(mqProvider, messageQueueName, statusQueueName);
        this.emailSender = emailSender;
        this.senderUserName = senderUserName;
        this.attachmentService = attachmentService;
        System.setProperty("mail.mime.splitlongparameters", "false");
    }

    public void send(Message message) {
        log.info("Sending email " + message);
        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setMessageId(message.getId());
        messageStatus.setTenantCode(message.getTenantCode());

        List<String> recipientsEmailList = message.getRecipients().stream()
                .map(Recipient::getEmail)
                .filter(StringUtils::hasText)
                .toList();
        if (!recipientsEmailList.isEmpty()) {
            try {
                MimeMessage mail = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
                helper.setTo(recipientsEmailList.toArray(String[]::new));
                helper.setSubject(message.getCaption());
                helper.setText(message.getText(), true);
                helper.setFrom(senderUserName);
                setAttachments(message, helper);
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

    private void setAttachments(Message message, MimeMessageHelper helper) throws MessagingException, IOException {
        if (!CollectionUtils.isEmpty(message.getAttachments()) && nonNull(attachmentService)) {
            for (AttachmentResponse attachment : message.getAttachments()) {
                String attachedFileName = MimeUtility.encodeText(attachment.getShortFileName());
                InputStream is = attachmentService.download(attachment.getFileName());
                ByteArrayDataSource dataSource = new ByteArrayDataSource(is, "application/octet-stream");
                helper.addAttachment(attachedFileName, dataSource);
            }
        }
    }
}
