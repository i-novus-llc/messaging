package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sms {
    @NotBlank
    private String phoneNumber;
    //if null, message will be send immediately
    private LocalDateTime timeToSend;
    @NotBlank
    private String templateCode;
    private Map<String, Object> placeholders;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public Map<String, Object> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Map<String, Object> placeholders) {
        this.placeholders = placeholders;
    }

    public LocalDateTime getTimeToSend() {
        return timeToSend;
    }

    public void setTimeToSend(LocalDateTime timeToSend) {
        this.timeToSend = timeToSend;
    }
}
