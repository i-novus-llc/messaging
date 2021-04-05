package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sms {
    private String phoneNumber;
    private LocalDateTime timeToSend;
    private String templateCode;
    private Map<String, Object> placeholders;
    private Boolean sendImmediately;

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

    public Boolean getSendImmediately() {
        return sendImmediately;
    }

    public void setSendImmediately(Boolean sendImmediately) {
        this.sendImmediately = sendImmediately;
    }
}
