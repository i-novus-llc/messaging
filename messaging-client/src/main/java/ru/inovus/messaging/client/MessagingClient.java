package ru.inovus.messaging.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.inovus.messaging.api.*;

import java.nio.charset.Charset;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class MessagingClient {
    private final String url;
    private final ObjectMapper mapper;

    public MessagingClient(String url) {
        this.url = url + "/messages";
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public ActionStatus sendMessage(MessageOutbox message) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
            headers.setContentType(mediaType);

            if (message.getMessage().getSentAt() == null)
                message.getMessage().setSentAt(LocalDateTime.now(Clock.systemUTC()));
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(message), headers);
            return restTemplate.postForObject(url, entity, ActionStatus.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        MessageOutbox messageOutbox = new MessageOutbox();
        Message message = new Message();
        message.setSentAt(LocalDateTime.now(Clock.systemUTC()));
        message.setCaption("хай");
        message.setText("привет");
        message.setAlertType(AlertType.POPUP);
        message.setSeverity(Severity.WARNING);
        messageOutbox.setMessage(message);
        messageOutbox.setRecipients(Collections.singletonList(new Recipient(RecipientType.ALL)));
        System.out.println(new MessagingClient("http://localhost:8081").sendMessage(messageOutbox));
    }
}
