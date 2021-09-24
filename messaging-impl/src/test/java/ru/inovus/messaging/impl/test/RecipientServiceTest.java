package ru.inovus.messaging.impl.test;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.impl.service.RecipientService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml"
        })
@EnableEmbeddedPg
public class RecipientServiceTest {
    @Autowired
    private RecipientService service;

    @Test
    public void testUpdateStatus() {
        RecipientCriteria criteria = new RecipientCriteria();

        List<Recipient> recipients = service.getRecipients(criteria).getContent();
        assertThat(recipients.size(), is(5));
        assertThat(recipients.get(0).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(1).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(2).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(3).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(4).getStatus(), is(MessageStatusType.SCHEDULED));

        // update recipients by systemId, username, messageId (update only one recipient)
        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setSystemId("sys1");
        messageStatus.setMessageId("a2bd666b-1684-4005-a10f-f14224f66d0a");
        messageStatus.setUsername("web1");
        messageStatus.setStatus(MessageStatusType.FAILED);
        messageStatus.setErrorMessage("Error");
        service.updateStatus(messageStatus);

        recipients = service.getRecipients(criteria).getContent();
        assertThat(recipients.size(), is(5));
        assertThat(recipients.get(0).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(1).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(2).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(3).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(4).getStatus(), is(MessageStatusType.FAILED));
        assertThat(recipients.get(4).getMessageId(), is(UUID.fromString("a2bd666b-1684-4005-a10f-f14224f66d0a")));
        assertThat(recipients.get(4).getUsername(), is("web1"));
        assertThat(recipients.get(4).getErrorMessage(), is("Error"));

        // update recipients by systemId, messageId (update all message recipients)
        messageStatus = new MessageStatus();
        messageStatus.setSystemId("sys1");
        messageStatus.setMessageId("d1450cd1-5b93-47fe-9e44-a3800476342e");
        messageStatus.setStatus(MessageStatusType.SENT);
        service.updateStatus(messageStatus);

        recipients = service.getRecipients(criteria).getContent();
        assertThat(recipients.size(), is(5));
        assertThat(recipients.get(0).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(1).getStatus(), is(MessageStatusType.SENT));
        assertThat(recipients.get(2).getStatus(), is(MessageStatusType.SENT));
        assertThat(recipients.get(3).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(4).getStatus(), is(MessageStatusType.FAILED));

        // update recipients by systemId, username (update all messages recipients by internal channel)
        // mustn't change status of not sent messages
        messageStatus = new MessageStatus();
        messageStatus.setSystemId("sys1");
        messageStatus.setUsername("web2");
        messageStatus.setStatus(MessageStatusType.READ);
        service.updateStatus(messageStatus);

        recipients = service.getRecipients(criteria).getContent();
        assertThat(recipients.size(), is(5));
        assertThat(recipients.get(0).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(1).getStatus(), is(MessageStatusType.READ));
        assertThat(recipients.get(2).getStatus(), is(MessageStatusType.SENT));
        assertThat(recipients.get(3).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(4).getStatus(), is(MessageStatusType.FAILED));
    }
}
