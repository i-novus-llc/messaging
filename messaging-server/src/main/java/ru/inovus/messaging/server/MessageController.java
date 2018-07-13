package ru.inovus.messaging.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.inovus.messaging.api.ActionStatus;
import ru.inovus.messaging.api.Message;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.impl.rest.MessagingCriteria;
import ru.inovus.messaging.impl.rest.MessagingResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final KafkaTemplate<String, MessageOutbox> kafkaTemplate;
    private final String topic;
    private final MessageService messageService;

    public MessageController(KafkaTemplate<String, MessageOutbox> kafkaTemplate,
                             @Value("${novus.messaging.topic}") String topic, MessageService messageService) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.messageService = messageService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<MessagingResponse> getMessages(@ModelAttribute MessagingCriteria criteria) {
        return new ResponseEntity<>(messageService.getMessages(criteria), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable("id") String id) {
        return new ResponseEntity<>(messageService.getMessage(id), HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<ActionStatus> sendMessage(final @RequestBody MessageOutbox message) {
        if (message.getMessage().getSentAt() == null)
            message.getMessage().setSentAt(LocalDateTime.now(Clock.systemUTC()));
        kafkaTemplate.send(topic, String.valueOf(System.currentTimeMillis()), message);
        return new ResponseEntity<>(new ActionStatus("Sent successfully"), HttpStatus.OK);
    }

    @PostMapping(path = "/{id}/read")
    @ResponseBody
    public ResponseEntity<ActionStatus> markRead(@PathVariable("id") String id) {
        messageService.markRead(id);
        return new ResponseEntity<>(new ActionStatus("Marked as read"), HttpStatus.OK);
    }
}
