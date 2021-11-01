package ru.inovus.messaging.impl.rest;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.MessageCriteria;
import ru.inovus.messaging.api.criteria.ProviderRecipientCriteria;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.*;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.impl.RecipientProvider;
import ru.inovus.messaging.impl.service.RecipientService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
    private RecipientService recipientService;

    @MockBean
    private MqProvider mqProvider;

    @MockBean
    private RecipientProvider recipientProvider;

    private final String TENANT_CODE = "tenant";

    @Test
    public void testCreateMessageFromTemplate() {
        MessageOutbox messageOutbox = new MessageOutbox();
        MessageCriteria criteria = new MessageCriteria();
        TemplateMessageOutbox templateMessage = new TemplateMessageOutbox();
        messageOutbox.setTemplateMessageOutbox(templateMessage);

        // template with empty templateCode shouldn't create message
        long count = messageRest.getMessages(TENANT_CODE, criteria).getTotalElements();
        messageRest.sendMessage(TENANT_CODE, messageOutbox);
        assertThat(messageRest.getMessages(TENANT_CODE, criteria).getTotalElements(), is(count));

        // create message from template
        doNothing().when(mqProvider).publish(any(Object.class), anyString());
        when(recipientProvider.getRecipients(any(ProviderRecipientCriteria.class))).thenAnswer(
            answer -> {
                ProviderRecipient recipient = new ProviderRecipient();
                recipient.setUsername("user1");
                return new PageImpl<>(Collections.singletonList(recipient));
            }
        );

        templateMessage.setTemplateCode("mt3");
        templateMessage.setSentAt(LocalDateTime.of(2022, 1, 15, 0, 0, 0));
        templateMessage.setUserNameList(Collections.singletonList("user1"));
        templateMessage.setPlaceholders(Map.of("{name}", "user1", "{count}", 25000));

        messageRest.sendMessage(TENANT_CODE, messageOutbox);
        criteria.setSentAtBegin(LocalDateTime.of(2022, 1, 15, 0, 0, 0));
        criteria.setSentAtEnd(LocalDateTime.of(2022, 1, 15, 0, 0, 0));
        Page<Message> messages = messageRest.getMessages(TENANT_CODE, criteria);
        assertThat(messages.getTotalElements(), is(1L));
        Message createdMessage = messages.getContent().get(0);
        assertThat(createdMessage.getCaption(), is("Congratulations, user1"));
        assertThat(createdMessage.getText(), is("You win $25000!"));
        assertThat(createdMessage.getSeverity(), is(Severity.ERROR));
        assertThat(createdMessage.getAlertType(), is(AlertType.HIDDEN));
        assertThat(createdMessage.getSentAt(), is(LocalDateTime.of(2022, 1, 15, 0, 0, 0)));
        assertThat(createdMessage.getChannel().getId(), is("email"));
        assertThat(createdMessage.getFormationType(), is(FormationType.AUTO));
        assertThat(createdMessage.getRecipientType(), is(RecipientType.RECIPIENT));

        RecipientCriteria recipientCriteria = new RecipientCriteria();
        recipientCriteria.setMessageId(UUID.fromString(createdMessage.getId()));
        Page<Recipient> recipients = recipientService.getRecipients(TENANT_CODE, recipientCriteria);
        assertThat(recipients.getTotalElements(), is(1L));
        assertThat(recipients.getContent().get(0).getUsername(), is("user1"));
    }
}
