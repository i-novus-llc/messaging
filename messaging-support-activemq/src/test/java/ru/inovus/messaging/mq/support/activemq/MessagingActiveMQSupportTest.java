package ru.inovus.messaging.mq.support.activemq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.api.queue.MqProvider;
import ru.inovus.messaging.api.queue.QueueMqConsumer;
import ru.inovus.messaging.api.queue.TopicMqConsumer;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author RMakhmutov
 * @since 02.04.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActiveMqProvider.class, MessagingActiveMQSupportTest.TestConfiguration.class})
public class MessagingActiveMQSupportTest {

    private static final String DEFAULT_SYSTEM_ID = "default";

    @Autowired
    private MqProvider mqProvider;

    private MessageOutbox receivedMessage;

    @Test
    public void testPassMessageTroughPubsubMQ() throws InterruptedException {
        final String WS_SESSION_ID = "ws-session-id";
        final String TOPIC_NAME = "test-topic-1";
        receivedMessage = null;

        mqProvider.subscribe(new TopicMqConsumer(WS_SESSION_ID, DEFAULT_SYSTEM_ID, "unique-user-id", TOPIC_NAME, messageOutbox -> receivedMessage = messageOutbox));

        mqProvider.publish(createNotice(), TOPIC_NAME);
        Thread.sleep(3000);
        assert receivedMessage != null;
        receivedMessage = null;

        mqProvider.unsubscribe(WS_SESSION_ID);
        mqProvider.publish(createNotice(), TOPIC_NAME);
        Thread.sleep(3000);
        assert receivedMessage == null;
    }

    private MessageOutbox receivedMessage2;
    private MessageOutbox receivedMessage3;

    @Test
    public void testPassMessageTroughSecondPubsubMQ() throws InterruptedException {
        final String WS_SESSION_ID_1 = "ws-session-id-1";
        final String WS_SESSION_ID_2 = "ws-session-id-2";
        final String TOPIC_NAME = "test-topic-2";
        receivedMessage2 = null;
        receivedMessage3 = null;

        mqProvider.subscribe(new TopicMqConsumer(WS_SESSION_ID_1, DEFAULT_SYSTEM_ID, "unique-user-id-1", TOPIC_NAME, messageOutbox -> {
            receivedMessage2 = messageOutbox;
            System.out.println("receivedMessage2");
        }));
        mqProvider.subscribe(new TopicMqConsumer(WS_SESSION_ID_2, DEFAULT_SYSTEM_ID, "unique-user-id-2", TOPIC_NAME, messageOutbox -> {
            receivedMessage3 = messageOutbox;
            System.out.println("receivedMessage3");
        }));

        mqProvider.publish(createNotice(), TOPIC_NAME);
        Thread.sleep(5000);

        mqProvider.unsubscribe(WS_SESSION_ID_1);
        mqProvider.unsubscribe(WS_SESSION_ID_2);

        /// assert that all of the consumers reads the message from the queue
        assert receivedMessage2 != null;
        assert receivedMessage3 != null;
    }

    private MessageOutbox receivedMessage4;

    @Test
    public void testPassMessageTroughProdconsMQ() throws InterruptedException {
        final String QUEUE_NAME = "email-queue-1";
        final String QUEUE_DESTINATION_NAME = "queue://" + QUEUE_NAME;
        final String QUEUE_CONSUMER_NAME = "emailConsumer";
        receivedMessage4 = null;

        mqProvider.subscribe(new QueueMqConsumer(QUEUE_NAME, messageOutbox -> receivedMessage4 = messageOutbox, QUEUE_CONSUMER_NAME));

        mqProvider.publish(createEmailNotice(), QUEUE_DESTINATION_NAME);
        Thread.sleep(3000);
        assert receivedMessage4 != null;
        receivedMessage4 = null;

        mqProvider.unsubscribe(QUEUE_CONSUMER_NAME);
        mqProvider.publish(createEmailNotice(), QUEUE_DESTINATION_NAME);
        Thread.sleep(3000);
        assert receivedMessage4 == null;
    }

    private MessageOutbox receivedMessage5;
    private MessageOutbox receivedMessage6;

    @Test
    public void testPassMessageTroughSecondProdconsMQ() throws InterruptedException {
        final String QUEUE_NAME = "email-queue-2";
        final String QUEUE_DESTINATION_NAME = "queue://" + QUEUE_NAME;
        final String QUEUE_CONSUMER_NAME_1 = "emailConsumer-1-2";
        final String QUEUE_CONSUMER_NAME_2 = "emailConsumer-1-2";
        receivedMessage5 = null;
        receivedMessage6 = null;

        mqProvider.subscribe(new QueueMqConsumer(QUEUE_NAME, messageOutbox -> receivedMessage5 = messageOutbox, QUEUE_CONSUMER_NAME_1));
        mqProvider.subscribe(new QueueMqConsumer(QUEUE_NAME, messageOutbox -> receivedMessage6 = messageOutbox, QUEUE_CONSUMER_NAME_2));

        mqProvider.publish(createEmailNotice(), QUEUE_DESTINATION_NAME);
        Thread.sleep(3000);

        mqProvider.unsubscribe(QUEUE_CONSUMER_NAME_1);
        mqProvider.unsubscribe(QUEUE_CONSUMER_NAME_2);

        /// assert that only one of the consumers reads the message from the queue
        assert (receivedMessage5 != null && receivedMessage6 == null) || (receivedMessage5 == null && receivedMessage6 != null);
    }

    @Test
    public void testRepassMessageTroughProdconsMQ() throws InterruptedException {
        final String QUEUE_NAME = "email-queue-3";
        final String QUEUE_DESTINATION_NAME = "queue://" + QUEUE_NAME;
        final String QUEUE_CONSUMER_NAME = "repassEmailConsumer";

        ReConsumer cons = new ReConsumer(2);

        mqProvider.subscribe(new QueueMqConsumer(QUEUE_NAME, cons, QUEUE_CONSUMER_NAME));

        mqProvider.publish(createEmailNotice(), QUEUE_DESTINATION_NAME);
        Thread.sleep(3000);

        assert cons.execCnt == 3;

        mqProvider.unsubscribe(QUEUE_CONSUMER_NAME);
    }

    @Test
    public void testMaxRepassMessageTroughProdconsMQ() throws InterruptedException {
        final String QUEUE_NAME = "email-queue-4";
        final String QUEUE_DESTINATION_NAME = "queue://" + QUEUE_NAME;
        final String QUEUE_CONSUMER_NAME = "repassEmailConsumer2";

        ReConsumer cons = new ReConsumer(-1);

        mqProvider.subscribe(new QueueMqConsumer(QUEUE_NAME, cons, QUEUE_CONSUMER_NAME));

        mqProvider.publish(createEmailNotice(), QUEUE_DESTINATION_NAME);
        Thread.sleep(3000);

        assert cons.execCnt == 4;

        mqProvider.unsubscribe(QUEUE_CONSUMER_NAME);
    }

    static class ReConsumer implements Consumer<MessageOutbox> {
        volatile int execCnt;
        int remainingFailsCnt;

        ReConsumer(int remainingFailsCnt) {
            this.remainingFailsCnt = remainingFailsCnt;
        }

        @Override
        public void accept(MessageOutbox messageOutbox) {
            execCnt++;

            if (remainingFailsCnt > 0) {
                remainingFailsCnt--;
                throw new RuntimeException();
            }

            if (remainingFailsCnt == -1) {
                throw new RuntimeException();
            }
        }
    }

    private MessageOutbox createNotice() {
        MessageOutbox outbox = new MessageOutbox();
        Message message = new Message();
        message.setInfoTypes(List.of(InfoType.NOTICE));
        message.setRecipientType(RecipientType.ALL);
        message.setSystemId(DEFAULT_SYSTEM_ID);
        outbox.setMessage(message);
        return outbox;
    }

    private MessageOutbox createEmailNotice() {
        MessageOutbox outbox = new MessageOutbox();
        Message message = new Message();
        message.setInfoTypes(List.of(InfoType.EMAIL));
        message.setRecipientType(RecipientType.ALL);
        outbox.setMessage(message);
        return outbox;
    }

    private MessageOutbox createAllNotice() {
        MessageOutbox outbox = new MessageOutbox();
        Message message = new Message();
        message.setInfoTypes(List.of(InfoType.EMAIL, InfoType.NOTICE));
        message.setRecipientType(RecipientType.USER);
        message.setRecipients(List.of(new Recipient("unique-user-id")));
        message.setSystemId(DEFAULT_SYSTEM_ID);
        outbox.setMessage(message);
        return outbox;
    }

    private MessageOutbox createInvalidNotice() {
        MessageOutbox outbox = new MessageOutbox();
        Message message = new Message();
        message.setInfoTypes(List.of());
        message.setRecipientType(RecipientType.ALL);
        message.setSystemId(DEFAULT_SYSTEM_ID);
        outbox.setMessage(message);
        return outbox;
    }

    @Configuration
    static class TestConfiguration {
        @Bean("objectMapper")
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
