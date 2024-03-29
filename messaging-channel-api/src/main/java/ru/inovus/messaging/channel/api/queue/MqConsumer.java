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

public interface MqConsumer {
    /**
     * Получение обработчика объекта очереди.
     * Набор действий, который будет производиться при попадании объекта в очередь
     *
     * @return Обработчик объекта очереди
     */
    Consumer messageHandler();

    /**
     * Получение имени очереди объектов
     *
     * @return Имя очереди объектов
     */
    String mqName();

    /**
     * Получение подписчика
     *
     * @return Подписчик
     */
    Serializable subscriber();
}
