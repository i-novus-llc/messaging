package ru.inovus.messaging.impl.rest;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
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
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.TemplateMessageOutbox;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.service.MessageService;
import ru.inovus.messaging.impl.service.RecipientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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

    @MockBean
    private MqProvider mqProvider;

    @MockBean
    private RecipientService recipientService;

    @Captor
    ArgumentCaptor<Message> messageArgumentCaptor;

    private String TENANT_CODE = "tenant";


    @BeforeEach
    public void before() {
        when(recipientService.getRecipientsByUsername(any())).thenReturn(List.of(getRecipient()));
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
        assertThat(dbStoredMessage.getChannel().getId(), is("web"));
        assertThat(dbStoredMessage.getTemplateCode(), is("mt1"));
        assertThat(dbStoredMessage.getRecipientType(), is(RecipientType.RECIPIENT));
        assertThat(dbStoredMessage.getSentAt(), is(LocalDateTime.parse("2007-12-03T10:15:30")));
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

    private Recipient getRecipient() {
        Recipient recipient = new Recipient("testUser");
        recipient.setName("Test User");
        recipient.setEmail("email@mail.novus");
        return recipient;
    }
}
