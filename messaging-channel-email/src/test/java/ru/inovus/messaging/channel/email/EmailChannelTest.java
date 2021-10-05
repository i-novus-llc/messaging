package ru.inovus.messaging.channel.email;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.MessageStatus;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.channel.api.queue.MqProvider;
import ru.inovus.messaging.channel.api.queue.QueueMqConsumer;
import ru.inovus.messaging.mq.support.kafka.KafkaMqProvider;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {TestApp.class},
        properties = {
                "novus.messaging.queue.status=test-status-queue",
                "novus.messaging.channel.email.queue=test-email-queue"})
@EmbeddedKafka
@ContextConfiguration(classes = KafkaMqProvider.class)
public class EmailChannelTest {

    @Autowired
    private MqProvider mqProvider;

    @Autowired
    private EmailChannel channel;

    @Value("${novus.messaging.queue.status}")
    private String statusQueue;

    @Autowired
    private EmailChannelProperties properties;

    @MockBean
    private JavaMailSender mailSender;


    @Test
    public void testSendMessageViaEmailQueue() throws Exception {
        Message message = new Message();
        message.setCaption("Test caption");
        message.setText("Message");

        Recipient recipient1 = new Recipient("email1");
        Recipient recipient2 = new Recipient("email2");
        Recipient recipient3 = new Recipient("email3");
        message.setRecipients(Arrays.asList(recipient1, recipient2, recipient3));

        CountDownLatch latch = new CountDownLatch(1);
        MimeMessage mimeMessage = mailSenderMimeMessageMock();
        doAnswer(a -> {
            latch.countDown();
            return "ignored";
        }).when(mailSender).send(mimeMessage);
        mqProvider.publish(message, properties.getQueue());
        latch.await();

        assertThat(mimeMessage.getSubject(), is(message.getCaption()));
        MimeMessageParser parser = new MimeMessageParser(mimeMessage);
        parser.parse();
        assertThat(parser.getHtmlContent(), is(message.getText()));

        Address[] allRecipients = mimeMessage.getAllRecipients();
        assertThat(allRecipients.length, is(message.getRecipients().size()));
        assertThat(allRecipients[0].toString(), is(recipient1.getUsername()));
        assertThat(allRecipients[1].toString(), is(recipient2.getUsername()));
        assertThat(allRecipients[2].toString(), is(recipient3.getUsername()));
    }

    @Test
    public void testSendMessageStatusSuccess() throws InterruptedException {
        Message message = new Message();
        message.setId("6f711616-1617-11ec-9621-0242ac130003");
        message.setCaption("Test caption");
        message.setText("Message");
        message.setSystemId("system-id");
        message.setRecipients(Collections.singletonList(new Recipient("email1")));

        CountDownLatch latch = new CountDownLatch(1);
        mailSenderMimeMessageMock();

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        mqProvider.subscribe(mqConsumer);
        channel.send(message);
        latch.await();
        mqProvider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getMessageId(), is(message.getId()));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.SENT));
        assertThat(receivedStatus[0].getSystemId(), is("system-id"));
    }

    @Test
    public void testSendMessageStatusFailed() throws InterruptedException {
        Message message = new Message();
        message.setId("6f711616-1617-11ec-9621-0242ac130002");
        message.setSystemId("system-id");
        message.setRecipients(Collections.singletonList(new Recipient("email1")));

        CountDownLatch latch = new CountDownLatch(1);
        mailSenderMimeMessageMock();

        final MessageStatus[] receivedStatus = new MessageStatus[1];
        QueueMqConsumer mqConsumer = new QueueMqConsumer(statusQueue, msg -> {
            receivedStatus[0] = (MessageStatus) msg;
            latch.countDown();
        }, statusQueue);

        mqProvider.subscribe(mqConsumer);
        channel.send(message);
        latch.await();
        mqProvider.unsubscribe(mqConsumer.subscriber());

        assertThat(receivedStatus[0], notNullValue());
        assertThat(receivedStatus[0].getMessageId(), is(message.getId()));
        assertThat(receivedStatus[0].getStatus(), is(MessageStatusType.FAILED));
        assertThat(receivedStatus[0].getErrorMessage(), is("Subject must not be null"));
        assertThat(receivedStatus[0].getSystemId(), is("system-id"));
    }

    private MimeMessage mailSenderMimeMessageMock() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);
        return mimeMessage;
    }
}
