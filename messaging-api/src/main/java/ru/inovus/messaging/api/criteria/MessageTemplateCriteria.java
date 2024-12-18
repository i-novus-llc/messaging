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
package ru.inovus.messaging.api.criteria;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.Severity;

import jakarta.ws.rs.QueryParam;

@Getter
@Setter
public class MessageTemplateCriteria extends BaseMessagingCriteria {

    @QueryParam("severity")
    @ApiParam("Уровень важности шаблона")
    private Severity severity;

    @QueryParam("alertType")
    @ApiParam("Способ отображения шаблона")
    private AlertType alertType;

    @QueryParam("infoType")
    @ApiParam("Канал отправки шаблона")
    private String channelCode;

    @QueryParam("name")
    @ApiParam("Имя шаблона")
    private String name;

    @QueryParam("enabled")
    @ApiParam("Признак включения")
    private Boolean enabled;

    @QueryParam("code")
    @ApiParam("Код шаблона")
    private String code;

    @QueryParam("codeAndName")
    @ApiParam("Код и наименование шаблона")
    private String codeAndName;
}
