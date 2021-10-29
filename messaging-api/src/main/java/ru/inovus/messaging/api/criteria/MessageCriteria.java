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
import ru.inovus.messaging.api.model.enums.Severity;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;

@Getter
@Setter
public class MessageCriteria extends BaseMessagingCriteria {

    @QueryParam("sentAtBegin")
    @ApiParam("Начальная дата отправки уведомления")
    private LocalDateTime sentAtBegin;

    @QueryParam("sentAtEnd")
    @ApiParam("Конечная дата отправки уведомления")
    private LocalDateTime sentAtEnd;

    @QueryParam("severity")
    @ApiParam("Уровень важности уведомления")
    private Severity severity;

    @QueryParam("infoType")
    @ApiParam("Канал отправки уведомления")
    private String channelCode;
}
