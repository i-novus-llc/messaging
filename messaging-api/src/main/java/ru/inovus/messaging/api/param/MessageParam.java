package ru.inovus.messaging.api.param;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageParam implements Serializable {

    private static final long serialVersionUID = 5475383823197483227L;

    String templateCode;
    LocalDateTime sendAt;
    List<UUID> domrfEmployeeId;
    List<UUID> bankEmployeeId;
    Map<String, Object> placeHolders;

    public MessageParam() {
        //
    }


    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public LocalDateTime getSendAt() {
        return sendAt;
    }

    public void setSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }

    public List<UUID> getDomrfEmployeeId() {
        return domrfEmployeeId;
    }

    public void setDomrfEmployeeId(List<UUID> domrfEmployeeId) {
        this.domrfEmployeeId = domrfEmployeeId;
    }

    public List<UUID> getBankEmployeeId() {
        return bankEmployeeId;
    }

    public void setBankEmployeeId(List<UUID> bankEmployeeId) {
        this.bankEmployeeId = bankEmployeeId;
    }

    public Map<String, Object> getPlaceHolders() {
        return placeHolders;
    }

    public void setPlaceHolders(Map<String, Object> placeHolders) {
        this.placeHolders = placeHolders;
    }
}
