/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.inovus.messaging.api.model;

import java.io.Serializable;
import java.util.List;

public class MessageSetting implements Serializable {

    private static final long serialVersionUID = 5475383823197483227L;

    private Integer id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType alertType;
    private List<InfoType> infoType;
    private FormationType formationType;
    private Component component;
    private String name;
    private Boolean disabled;
    private String code;

    public MessageSetting() {
    }

    public MessageSetting(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public List<InfoType> getInfoType() {
        return infoType;
    }

    public void setInfoType(List<InfoType> infoType) {
        this.infoType = infoType;
    }

    public FormationType getFormationType() {
        return formationType;
    }

    public void setFormationType(FormationType formationType) {
        this.formationType = formationType;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getSeverityName() {
        return this.severity != null ? severity.getName() : null;
    }

    public String getAlertTypeName() {
        return alertType != null ? alertType.getName() : null;
    }

    public String getFormationTypeName() {
        return formationType != null ? formationType.getName() : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
