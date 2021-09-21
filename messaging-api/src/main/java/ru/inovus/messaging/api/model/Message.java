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
import ru.inovus.messaging.api.model.enums.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {
    private String id;
    private String caption;
    private String text;
    private Severity severity;
    private AlertType alertType;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Channel channel;
    private Component component;
    private FormationType formationType;
    private RecipientType recipientType;
    private String systemId;
    private List<Recipient> recipients;
    private Map<String, String> data;
    private String notificationType;
    private String objectId;
    private String objectType;
}
