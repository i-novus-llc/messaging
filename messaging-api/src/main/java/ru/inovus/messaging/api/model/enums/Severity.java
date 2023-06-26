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
package ru.inovus.messaging.api.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Уровень важности уведомления
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Severity {
    INFO("Информация"),
    WARNING("Предупреждение"),
    DANGER("Ошибка"),
    PRIMARY("Важный");

    private String name;

    Severity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return name();
    }
}
