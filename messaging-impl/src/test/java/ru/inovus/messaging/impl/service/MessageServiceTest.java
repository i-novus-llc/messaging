package ru.inovus.messaging.impl.service;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Channel;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class MessageServiceTest {

    @Autowired
    private MessageService service;

    private static final String TENANT_CODE = "tenant";

    @Test
    public void testGetMessages() {
        MessageCriteria criteria = new MessageCriteria();

        Page<Message> messages = service.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(2L));
        // order by sentAt desc
        assertThat(messages.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 12, 31, 0, 0, 0)));
        assertThat(messages.getContent().get(1).getSentAt(), is(LocalDateTime.of(2021, 8, 15, 0, 0, 0)));

        // filter by sentAt start
        criteria.setSentAtBegin(LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        messages = service.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(1L));
        assertThat(messages.getContent().get(0).getText(), is("Message2"));
        assertThat(messages.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 12, 31, 0, 0, 0)));

        // filter by sentAt end
        criteria = new MessageCriteria();
        criteria.setSentAtEnd(LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        messages = service.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(1L));
        assertThat(messages.getContent().get(0).getText(), is("Message1"));
        assertThat(messages.getContent().get(0).getSentAt(), is(LocalDateTime.of(2021, 8, 15, 0, 0, 0)));

        // filter by severity
        criteria = new MessageCriteria();
        criteria.setSeverity(Severity.ERROR);
        messages = service.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(1L));
        assertThat(messages.getContent().get(0).getText(), is("Message1"));
        assertThat(messages.getContent().get(0).getSeverity(), is(Severity.ERROR));

        // filter by channel code
        criteria = new MessageCriteria();
        criteria.setChannelCode("email");
        messages = service.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(0L));
        criteria.setChannelCode("web");
        messages = service.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(2L));
    }

    @Test
    public void testGetMessage() {
        Message message = service.getMessage(UUID.fromString("d1450cd1-5b93-47fe-9e44-a3800476342e"));
        assertThat(message.getId(), is("d1450cd1-5b93-47fe-9e44-a3800476342e"));
        assertThat(message.getText(), is("Message2"));
        assertThat(message.getSeverity(), is(Severity.INFO));
        assertThat(message.getAlertType(), is(AlertType.POPUP));
        assertThat(message.getSentAt(), is(LocalDateTime.of(2021, 12, 31, 0, 0, 0)));
        assertThat(message.getRecipientType(), is(RecipientType.RECIPIENT));
        assertThat(message.getChannel().getId(), is("web"));
        assertThat(message.getRecipients().size(), is(2));
        assertThat(message.getRecipients().get(0).getId(), is(3L));
        assertThat(message.getRecipients().get(1).getId(), is(4L));
    }

    @Test
    public void testCreateMessage() {
        Message newMessage = new Message();
        newMessage.setTenantCode("tenant2");
        newMessage.setText("text");
        newMessage.setCaption("caption");
        newMessage.setSeverity(Severity.WARNING);
        newMessage.setAlertType(AlertType.HIDDEN);
        newMessage.setRecipientType(RecipientType.RECIPIENT);
        newMessage.setChannel(new Channel("email", "Email", "email-queue"));
        Recipient recipient = new Recipient();
        recipient.setName("name");
        recipient.setUsername("username");

        String newMessageId = service.createMessage(newMessage, recipient).getId();
        Message message = service.getMessage(UUID.fromString(newMessageId));
        assertThat(message.getText(), is(newMessage.getText()));
        assertThat(message.getCaption(), is(newMessage.getCaption()));
        assertThat(message.getSeverity(), is(newMessage.getSeverity()));
        assertThat(message.getAlertType(), is(newMessage.getAlertType()));
        assertThat(message.getRecipientType(), is(newMessage.getRecipientType()));
        assertThat(message.getChannel().getId(), is(newMessage.getChannel().getId()));
        assertThat(message.getRecipients().size(), is(1));
        assertThat(message.getRecipients().get(0).getName(), is(recipient.getName()));
        assertThat(message.getRecipients().get(0).getUsername(), is(recipient.getUsername()));
    }
}
