package ru.inovus.messaging;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.Assert;
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
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageOutbox;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.TemplateMessageOutbox;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.rest.MessageRestImpl;
import ru.inovus.messaging.impl.service.MessageService;
import ru.inovus.messaging.impl.service.RecipientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        properties = "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml")
@EnableEmbeddedPg
public class MessageRestImplTest {

    @Autowired
    MessageRestImpl messageRest;

    @Autowired
    MessageService messageService;

    @MockBean
    MqProvider mqProvider;

    @MockBean
    private RecipientService recipientService;

    @Captor
    ArgumentCaptor<Message> messageArgumentCaptor;

    private String TENANT_CODE = "tenant";

    @BeforeEach
    public void before() {
        Mockito.when(recipientService.getRecipientsByUsername(any())).thenReturn(List.of(getRecipient()));
    }

    @Test
    public void testTemplatedMessage() {
        messageRest.sendMessage(TENANT_CODE, getTemplatedMessage());

        Mockito.verify(mqProvider).publish(messageArgumentCaptor.capture(), any());
        Message capturedMessage = messageArgumentCaptor.getValue();

        Assert.assertEquals(capturedMessage.getCaption(), "Message template 1 Первый тестовый параметр второй параметр для теста Первый тестовый параметр");
        Assert.assertEquals(capturedMessage.getText(), "Some text with второй параметр для теста Первый тестовый параметр второй параметр для теста");
        Assert.assertEquals(capturedMessage.getSeverity(), Severity.INFO);
        Assert.assertEquals(capturedMessage.getRecipients().get(0).getName(), "тестовый пользователь");
        Assert.assertEquals(capturedMessage.getRecipients().get(0).getUsername(), "testUser");
        Assert.assertEquals(capturedMessage.getRecipients().get(0).getEmail(), "email@mail.novus");
        Assert.assertEquals(capturedMessage.getTenantCode(), TENANT_CODE);
        Assert.assertEquals(capturedMessage.getAlertType(), AlertType.POPUP);

        Message dbStoredMessage = messageService.getMessage(UUID.fromString(capturedMessage.getId()));
        Assert.assertEquals(dbStoredMessage.getCaption(), capturedMessage.getCaption());
        Assert.assertEquals(dbStoredMessage.getText(), capturedMessage.getText());
        Assert.assertEquals(dbStoredMessage.getSeverity(), capturedMessage.getSeverity());
        Assert.assertEquals(dbStoredMessage.getRecipients().get(0).getName(), capturedMessage.getRecipients().get(0).getName());
        Assert.assertEquals(dbStoredMessage.getRecipients().get(0).getUsername(), capturedMessage.getRecipients().get(0).getUsername());
        Assert.assertEquals(dbStoredMessage.getTenantCode(), capturedMessage.getTenantCode());
        Assert.assertEquals(dbStoredMessage.getAlertType(), capturedMessage.getAlertType());
        Assert.assertEquals(dbStoredMessage.getChannel().getId(), "web");
        Assert.assertEquals(dbStoredMessage.getTemplateCode(), "mt1");
        Assert.assertEquals(dbStoredMessage.getRecipientType(), RecipientType.RECIPIENT);
        Assert.assertEquals(dbStoredMessage.getFormationType(), FormationType.HAND);
        Assert.assertEquals(dbStoredMessage.getSentAt(), LocalDateTime.parse("2007-12-03T10:15:30"));
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
        templateMessageOutbox.setTemplateCode("mt1");
        templateMessageOutbox.setSentAt(LocalDateTime.parse("2007-12-03T10:15:30"));
        templateMessageOutbox.setUserNameList(List.of("testUser"));
        templateMessageOutbox.setPlaceholders(Map.of("%{param1}", "Первый тестовый параметр",
                "%{param2}", "второй параметр для теста"));

        MessageOutbox messageOutbox = new MessageOutbox();
        messageOutbox.setTemplateMessageOutbox(templateMessageOutbox);
        return messageOutbox;
    }

    private Recipient getRecipient() {
        Recipient recipient = new Recipient("testUser");
        recipient.setName("тестовый пользователь");
        recipient.setEmail("email@mail.novus");
        return recipient;
    }
}
