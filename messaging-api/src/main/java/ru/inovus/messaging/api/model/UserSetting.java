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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 5475383823197483228L;

    private Integer id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType defaultAlertType;
    private AlertType alertType;
    private ChannelType defaultChannelType;
    private ChannelType channelType;
    private Component component;
    private String name;
    private Boolean disabled;
    private String templateCode;
    private boolean defaultSetting;

    public UserSetting() {
    }

    public UserSetting(Integer id) {
        this.id = id;
    }

    public String getSeverityName() {
        return this.severity != null ? severity.getName() : null;
    }

    public String getAlertTypeName() {
        return alertType != null ? alertType.getName() : null;
    }

    public String getDefaultAlertTypeName() {
        return defaultAlertType != null ? defaultAlertType.getName() : null;
    }
}
