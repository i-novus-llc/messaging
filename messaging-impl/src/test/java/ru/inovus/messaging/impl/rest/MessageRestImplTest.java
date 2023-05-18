package ru.inovus.messaging.impl.rest;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.api.rest.SecurityProviderRest;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.MessageService;
import ru.inovus.messaging.impl.service.RecipientService;
import ru.inovus.messaging.impl.util.MessageHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class MessageRestImplTest {

    @Autowired
    private MessageRestImpl messageRest;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageHelper messageHelper;

    @MockBean
    private MqProvider mqProvider;

    @MockBean
    private RecipientService recipientService;

    @MockBean
    private SecurityProviderRest securityProviderRest;

    @Captor
    ArgumentCaptor<Message> messageArgumentCaptor;

    private String TENANT_CODE = "tenant";


    @BeforeEach
    public void before() {
        when(recipientService.getRecipientsByUsername(any())).thenReturn(List.of(getRecipient()));
        when(recipientService.getAll()).thenReturn(getRecipients());
    }

    @Test
    public void testTemplatedMessage() {
        messageRest.sendMessage(TENANT_CODE, getTemplatedMessage());

        Mockito.verify(mqProvider).publish(messageArgumentCaptor.capture(), any());
        Message publishedMessage = messageArgumentCaptor.getValue();

        assertThat(publishedMessage.getCaption(), is("Congratulations, User"));
        assertThat(publishedMessage.getText(), is("Hello, User. You win $25000!"));
        assertThat(publishedMessage.getSeverity(), is(Severity.ERROR));
        Recipient recipient = publishedMessage.getRecipients().get(0);
        assertThat(recipient.getName(), is("Test User"));
        assertThat(recipient.getUsername(), is("testUser"));
        assertThat(recipient.getEmail(), is("email@mail.novus"));
        assertThat(publishedMessage.getTenantCode(), is(TENANT_CODE));
        assertThat(publishedMessage.getAlertType(), is(AlertType.HIDDEN));

        Message dbStoredMessage = messageService.getMessage(UUID.fromString(publishedMessage.getId()));
        assertThat(dbStoredMessage.getCaption(), is(publishedMessage.getCaption()));
        assertThat(dbStoredMessage.getText(), is(publishedMessage.getText()));
        assertThat(dbStoredMessage.getSeverity(), is(publishedMessage.getSeverity()));
        assertThat(dbStoredMessage.getRecipients().get(0).getName(), is(recipient.getName()));
        assertThat(dbStoredMessage.getRecipients().get(0).getUsername(), is(recipient.getUsername()));
        assertThat(dbStoredMessage.getTenantCode(), is(publishedMessage.getTenantCode()));
        assertThat(dbStoredMessage.getAlertType(), is(publishedMessage.getAlertType()));
        assertThat(dbStoredMessage.getChannel().getId(), is("email"));
        assertThat(dbStoredMessage.getTemplateCode(), is("mt3"));
        assertThat(dbStoredMessage.getRecipientType(), is(RecipientType.RECIPIENT));
        assertThat(dbStoredMessage.getSentAt(), is(LocalDateTime.parse("2007-12-03T10:15:30")));
    }

    @Test
    public void testEmptyRecipientsMessage() {
        Exception exception = assertThrows(UserException.class, () -> {
            messageRest.sendMessage(TENANT_CODE, getEmptyRecipientsMessage());
        });
        assertThat(exception.getMessage(), is(messageHelper.obtainMessage("messaging.exception.message.recipients.empty")));
    }

    @Test
    public void testEmptyTemplateCodeAndWrongTenant() {
        // template with empty templateCode shouldn't create message
        long count = messageRest.getMessages(TENANT_CODE, new MessageCriteria()).getTotalElements();

        MessageOutbox outboxNullTemplateCode = getTemplatedMessage();
        outboxNullTemplateCode.getTemplateMessageOutbox().setTemplateCode(null);
        messageRest.sendMessage(TENANT_CODE, outboxNullTemplateCode);
        assertThat(messageRest.getMessages(TENANT_CODE, new MessageCriteria()).getTotalElements(), is(count));
        Mockito.verify(mqProvider, Mockito.never()).publish(messageArgumentCaptor.capture(), any());

        // template with wrongTenant shouldn't create message
        MessageOutbox outbox = getTemplatedMessage();
        messageRest.sendMessage("wrong_tenant_code", outbox);
        assertThat(messageRest.getMessages(TENANT_CODE, new MessageCriteria()).getTotalElements(), is(count));
        Mockito.verify(mqProvider, Mockito.never()).publish(messageArgumentCaptor.capture(), any());
    }

    private MessageOutbox getTemplatedMessage() {
        TemplateMessageOutbox templateMessageOutbox = new TemplateMessageOutbox();
        templateMessageOutbox.setTemplateCode("mt3");
        templateMessageOutbox.setSentAt(LocalDateTime.parse("2007-12-03T10:15:30"));
        templateMessageOutbox.setUserNameList(List.of("testUser"));
        templateMessageOutbox.setPlaceholders(Map.of("%{name}", "User", "%{count}", "25000"));

        MessageOutbox messageOutbox = new MessageOutbox();
        messageOutbox.setTemplateMessageOutbox(templateMessageOutbox);
        return messageOutbox;
    }

    private MessageOutbox getEmptyRecipientsMessage() {
        Message message = new Message();
        message.setText("test text");
        message.setSeverity(Severity.INFO);
        message.setAlertType(AlertType.POPUP);
        message.setRecipientType(RecipientType.RECIPIENT);
        message.setChannel(new Channel("web", "web", "web_queue"));
        MessageOutbox messageOutbox = new MessageOutbox();
        messageOutbox.setMessage(message);
        return messageOutbox;
    }

    private Recipient getRecipient() {
        Recipient recipient = new Recipient("testUser");
        recipient.setName("Test User");
        recipient.setEmail("email@mail.novus");
        return recipient;
    }

    private List<Recipient> getRecipients() {
        Recipient firstUser = new Recipient();
        firstUser.setEmail("test1@test.ru");
        firstUser.setUsername("firstUsername");

        Recipient secondUser = new Recipient();
        secondUser.setEmail("test2@test.ru");
        secondUser.setUsername("secondUsername");

        return List.of(firstUser, secondUser);
    }
}
