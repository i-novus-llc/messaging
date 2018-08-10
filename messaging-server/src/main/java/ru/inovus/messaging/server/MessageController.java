package ru.inovus.messaging.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inovus.messaging.api.ActionStatus;
import ru.inovus.messaging.api.Message;
import ru.inovus.messaging.api.MessageOutbox;
import ru.inovus.messaging.impl.MessageService;
import ru.inovus.messaging.impl.rest.MessagingCriteria;
import ru.inovus.messaging.impl.rest.MessagingResponse;
import ru.inovus.messaging.server.mq.MqProvider;

import java.time.Clock;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final Long timeout;
    private final MqProvider mqProvider;

    public MessageController(MessageService messageService,
                             @Value("${novus.messaging.timeout}") Long timeout,
                             MqProvider mqProvider) {
        this.messageService = messageService;
        this.timeout = timeout;
        this.mqProvider = mqProvider;
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
        mqProvider.publish(message);
        return new ResponseEntity<>(new ActionStatus("Sent successfully"), HttpStatus.OK);
    }

    @PostMapping(path = "/{id}/read")
    @ResponseBody
    public ResponseEntity<ActionStatus> markRead(@PathVariable("id") String id) {
        messageService.markRead(id);
        return new ResponseEntity<>(new ActionStatus("Marked as read"), HttpStatus.OK);
    }
}
