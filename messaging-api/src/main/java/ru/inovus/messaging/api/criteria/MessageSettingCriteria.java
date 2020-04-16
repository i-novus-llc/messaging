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
package ru.inovus.messaging.api.criteria;

import ru.inovus.messaging.api.model.*;

import javax.ws.rs.QueryParam;

public class MessageSettingCriteria extends BaseMessagingCriteria {
    private static final long serialVersionUID = 7609048158169451957L;

    @QueryParam("component.id")
    private Integer componentId;

    @QueryParam("severity.id")
    private Severity severity;

    @QueryParam("alertType.id")
    private AlertType alertType;

    @QueryParam("infoType.id")
    private InfoType infoType;

    @QueryParam("name")
    private String name;

    @QueryParam("formationType.id")
    private FormationType formationType;

    @QueryParam("enabled.id")
    private YesNo enabled;

    @QueryParam("code")
    private String code;

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
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

    public InfoType getInfoType() {
        return infoType;
    }

    public void setInfoType(InfoType infoType) {
        this.infoType = infoType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormationType getFormationType() {
        return formationType;
    }

    public void setFormationType(FormationType formationType) {
        this.formationType = formationType;
    }

    public void setEnabled(YesNo enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled != null ? enabled.getValue() : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
