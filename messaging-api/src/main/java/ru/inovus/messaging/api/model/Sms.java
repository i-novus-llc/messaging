package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sms {
    @NotBlank
    private String phoneNumber;
    //if null, message will be send immediately
    private LocalDateTime timeToSend;
    @NotBlank
    private String templateCode;
    private Map<String, Object> placeholders;
}
