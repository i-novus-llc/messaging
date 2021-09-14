package ru.inovus.messaging.email.channel;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.Recipient;
import ru.inovus.messaging.channel.api.queue.MqProvider;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EmailChannel.class)
@ContextConfiguration(classes = MqProvider.class)
public class TestEmailChannel {

    @MockBean
    private MqProvider provider;

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private EmailChannel channel;


    @Test
    public void testSendMessage() throws Exception {
        Message message = new Message();
        message.setCaption("Test caption");
        message.setText("Message");

        Recipient recipient1 = new Recipient();
        recipient1.setEmail("email1");
        Recipient recipient2 = new Recipient();
        recipient2.setEmail("email2");
        Recipient recipient3 = new Recipient();
        recipient3.setEmail("email3");
        message.setRecipients(Arrays.asList(recipient1, recipient2, recipient3));


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(mimeMessage);
        channel.send(message);

        assertThat(mimeMessage.getSubject(), is(message.getCaption()));
        MimeMessageParser parser = new MimeMessageParser(mimeMessage);
        parser.parse();
        assertThat(parser.getHtmlContent(), is(message.getText()));

        Address[] allRecipients = mimeMessage.getAllRecipients();
        assertThat(allRecipients.length, is(message.getRecipients().size()));
        assertThat(allRecipients[0].toString(), is(recipient1.getEmail()));
        assertThat(allRecipients[1].toString(), is(recipient2.getEmail()));
        assertThat(allRecipients[2].toString(), is(recipient3.getEmail()));
    }
}
