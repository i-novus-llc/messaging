package ru.inovus.messaging.mq.support.kafka.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.impl.EmailSender;

import java.io.IOException;

@Component
public class KafkaEmailTopicReceiver {

    Logger log = LoggerFactory.getLogger(KafkaEmailTopicReceiver.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailSender emailSender;

    @KafkaListener(topics = "${email.topic}")
    public void receive(String message) {
        try {
            MessageOutbox mo = objectMapper.readValue(message, MessageOutbox.class);
            emailSender.send(mo);
            log.info("Message with id=" + mo.getMessage().getId() + " was send");
        } catch (IOException e) {
            log.error("Error with convert message from Json to MessageOutbox");
            e.printStackTrace();
        }

    }
}
