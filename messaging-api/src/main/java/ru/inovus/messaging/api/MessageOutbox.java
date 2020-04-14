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
package ru.inovus.messaging.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.inovus.messaging.api.model.Message;
import ru.inovus.messaging.api.model.TemplateMessageOutbox;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageOutbox implements Serializable {

    private static final long serialVersionUID = -5227708517651903498L;

    private Message message;
    private TemplateMessageOutbox templateMessageOutbox;

    public MessageOutbox() {
        //
    }

    public MessageOutbox(Message message, TemplateMessageOutbox messageOutbox){
        this.message = message;
        this.templateMessageOutbox = messageOutbox;
    }

    public MessageOutbox(Message message) {
        this.message = message;
    }

    public MessageOutbox(TemplateMessageOutbox templateMessageOutbox) {
        this.templateMessageOutbox = templateMessageOutbox;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public TemplateMessageOutbox getTemplateMessageOutbox() {
        return templateMessageOutbox;
    }

    public void setTemplateMessageOutbox(TemplateMessageOutbox templateMessageOutbox) {
        this.templateMessageOutbox = templateMessageOutbox;
    }
}
