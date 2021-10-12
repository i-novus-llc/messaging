package ru.inovus.messaging;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.FeedCount;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.impl.service.RecipientService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.liquibase.change-log: classpath:/messaging-db/test-base-changelog.xml",
                "novus.messaging.queue.feed-count: test-feed-count-queue"
        })
@EmbeddedKafka
@EnableEmbeddedPg
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
public class RecipientServiceTest {
    @Autowired
    private RecipientService service;

    @Autowired
    private MqProvider mqProvider;

    @Value("${novus.messaging.queue.feed-count}")
    private String feedCountQueue;

    @Test
    public void testUpdateStatus() throws InterruptedException {
        RecipientCriteria criteria = new RecipientCriteria();

        List<Recipient> recipients = service.getRecipients(criteria).getContent();
        assertThat(recipients.size(), is(5));
        assertThat(recipients.get(0).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(1).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(2).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(3).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(4).getStatus(), is(MessageStatusType.SCHEDULED));

        // update recipients by tenantCode, username, messageId (update only one recipient)
        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setTenantCode("tenant");
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
        assertThat(recipients.get(4).getMessageId(), is(UUID.fromString("a2bd666b-1684-4005-a10f-f14224f66d0a")));
        assertThat(recipients.get(4).getUsername(), is("web1"));
        assertThat(recipients.get(4).getErrorMessage(), is("Error"));

        // update recipients by tenantCode, messageId (update all message recipients)
        messageStatus = new MessageStatus();
        messageStatus.setTenantCode("tenant");
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

        // update recipients by tenantCode, username (update all messages recipients by internal channel)
        // mustn't change status of not sent messages
        messageStatus = new MessageStatus();
        messageStatus.setTenantCode("tenant");
        messageStatus.setUsername("web2");
        messageStatus.setStatus(MessageStatusType.READ);

        CountDownLatch latch = new CountDownLatch(1);
        final FeedCount[] feedCount = new FeedCount[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(feedCountQueue, msg -> {
            feedCount[0] = (FeedCount) msg;
            latch.countDown();
        }, feedCountQueue);

        mqProvider.subscribe(mqConsumer);

        service.updateStatus(messageStatus);
        latch.await();

        recipients = service.getRecipients(criteria).getContent();
        assertThat(recipients.size(), is(5));
        assertThat(recipients.get(0).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(1).getStatus(), is(MessageStatusType.READ));
        assertThat(recipients.get(2).getStatus(), is(MessageStatusType.SENT));
        assertThat(recipients.get(3).getStatus(), is(MessageStatusType.SCHEDULED));
        assertThat(recipients.get(4).getStatus(), is(MessageStatusType.FAILED));

        assertThat(feedCount[0].getTenantCode(), is("tenant"));
        assertThat(feedCount[0].getUsername(), is("web2"));
        assertThat(feedCount[0].getCount(), is(0));
    }
}
