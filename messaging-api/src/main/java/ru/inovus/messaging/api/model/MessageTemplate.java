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
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.Severity;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageTemplate implements Serializable {

    @ApiModelProperty("Идентификатор шаблона")
    private Integer id;

    @ApiModelProperty("Заголовок шаблона")
    private String caption;

    @ApiModelProperty("Текст шаблона")
    private String text;

    @ApiModelProperty("Уровень важности шаблона")
    private Severity severity;

    @ApiModelProperty("Способ отображения шаблона")
    private AlertType alertType;

    @ApiModelProperty("Канал отправки")
    private Channel channel;

    @ApiModelProperty("Способ формирования уведомления")
    private FormationType formationType;

    @ApiModelProperty("Имя шаблона")
    private String name;

    @ApiModelProperty("Признак включения")
    private Boolean enabled;

    @ApiModelProperty("Код шаблона")
    private String code;

    public MessageTemplate(Integer id) {
        this.id = id;
    }
}
