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
package ru.inovus.messaging.channel.api.queue;

import java.io.Serializable;
import java.util.function.Consumer;

public class QueueMqConsumer implements MqConsumer {
    private final String queueName;
    private final Consumer<Object> messageHandler;
    private final Serializable consumerUniqueName;

    public QueueMqConsumer(String queueName, Consumer<Object> messageHandler, Serializable consumerUniqueName) {
        this.queueName = queueName;
        this.messageHandler = messageHandler;
        this.consumerUniqueName = consumerUniqueName;
    }

    @Override
    public Consumer<Object> messageHandler() {
        return messageHandler;
    }

    @Override
    public String mqName() {
        return queueName;
    }

    @Override
    public Serializable subscriber() {
        return consumerUniqueName;
    }
}
