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
package ru.inovus.messaging.api.queue;

import ru.inovus.messaging.api.model.MessageOutbox;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * @author RMakhmutov
 * @since 02.04.2019
 */
public class TopicMqConsumer implements MqConsumer {
    private final String topicName;
    private final Consumer<MessageOutbox> messageHandler;
    private final Serializable subscriber;

    public final String systemId;
    public final String authToken;

    public TopicMqConsumer(Serializable subscriber, String systemId, String authToken, String topicName, Consumer<MessageOutbox> messageHandler) {
        this.topicName = topicName;
        this.messageHandler = messageHandler;
        this.subscriber = subscriber;
        this.systemId = systemId;
        this.authToken = authToken;
    }

    @Override
    public Consumer<MessageOutbox> messageHandler() {
        return messageHandler;
    }

    @Override
    public String mqName() {
        return topicName;
    }

    @Override
    public Serializable subscriber() {
        return subscriber;
    }
}
