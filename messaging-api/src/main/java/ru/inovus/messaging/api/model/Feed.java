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
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Feed implements Serializable {

    private String id;
    private String caption;
    private String text;
    private Severity severity;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Component component;
    private String systemId;

    public Feed() {
    }

    public Feed(String id) {
        this.id = id;
    }

    public String getSeverityName() {
        return this.severity != null ? severity.getName() : null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", text='" + text + '\'' +
                ", severity=" + severity +
                ", sentAt=" + sentAt +
                ", readAt=" + readAt +
                '}';
    }
}
