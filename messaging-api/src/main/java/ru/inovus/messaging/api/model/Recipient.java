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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.inovus.messaging.api.model.enums.MessageStatusType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Recipient implements Serializable {

    @ApiModelProperty("Идентификатор получателя")
    private Integer id;

    @ApiModelProperty("Имя получателя")
    private String name;

    @ApiModelProperty("Имя пользователя получателя")
    private String username;

    @ApiModelProperty("Email получателя")
    private String email;

    @ApiModelProperty("Время установки текущего статуса")
    private LocalDateTime statusTime;

    @ApiModelProperty("Идентификатор уведомления")
    private UUID messageId;

    @ApiModelProperty("Дата отправки уведомления")
    private LocalDateTime departuredAt;

    @ApiModelProperty("Текущий статус")
    private MessageStatusType status;

    @ApiModelProperty("Сообщение ошибки отправки")
    private String errorMessage;


    public Recipient(String username) {
        this.username = username;
    }
}