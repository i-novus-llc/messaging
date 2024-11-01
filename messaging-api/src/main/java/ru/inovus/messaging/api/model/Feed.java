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
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.enums.Severity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Feed implements Serializable {

    @ApiModelProperty("Идентификатор уведомления")
    private String id;

    @ApiModelProperty("Заголовок уведомления")
    private String caption;

    @ApiModelProperty("Текст уведомления")
    private String text;

    @ApiModelProperty("Уровень важности уведомления")
    private Severity severity;

    @ApiModelProperty("Дата отправки уведомления")
    private LocalDateTime sentAt;

    @ApiModelProperty("Дата прочтения уведомления")
    private LocalDateTime readAt;

    @ApiModelProperty("Код шаблона")
    private String templateCode;
}
