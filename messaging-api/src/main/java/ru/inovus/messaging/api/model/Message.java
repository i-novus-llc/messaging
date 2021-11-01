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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Message implements Serializable {

    @ApiModelProperty("Идентификатор уведомления")
    private String id;

    @ApiModelProperty("Заголовок уведомления")
    private String caption;

    @ApiModelProperty("Текст уведомления")
    private String text;

    @ApiModelProperty("Уровень важности уведомления")
    private Severity severity;

    @ApiModelProperty("Способ отображения уведомления")
    private AlertType alertType;

    @ApiModelProperty("Дата отправки")
    private LocalDateTime sentAt;

    @ApiModelProperty("Дата прочтения")
    private LocalDateTime readAt;

    @ApiModelProperty("Канал отправки")
    private Channel channel;

    @ApiModelProperty("Тип получателей уведомления")
    private RecipientType recipientType;

    @ApiModelProperty("Список получателей")
    private List<Recipient> recipients;

    @ApiModelProperty("Код шаблона уведомления")
    private String templateCode;

    @JsonIgnore
    private String tenantCode;
}
