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
package ru.inovus.messaging.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import ru.inovus.messaging.api.ActionStatus;
import ru.inovus.messaging.api.MessageOutbox;

import java.nio.charset.Charset;
import java.time.Clock;
import java.time.LocalDateTime;

public class MessagingClient {
    private final String url;
    private final ObjectMapper mapper;

    public MessagingClient(String url) {
        this.url = url + "/messages";
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public ActionStatus sendMessage(MessageOutbox message) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
            headers.setContentType(mediaType);
            if (message.getMessage() != null && message.getMessage().getSentAt() == null)
                message.getMessage().setSentAt(LocalDateTime.now(Clock.systemUTC()));
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(message), headers);
            return restTemplate.postForObject(url, entity, ActionStatus.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
