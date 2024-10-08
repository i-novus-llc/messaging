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

import jakarta.ws.rs.QueryParam;
import java.util.List;

@Getter
@Setter
public class ProviderRecipientCriteria extends BaseMessagingCriteria {

    @QueryParam("username")
    @ApiParam("Имя пользователя получателя")
    private String username;

    @QueryParam("name")
    @ApiParam("Имя получателя")
    private String name;

    @QueryParam("fio")
    @ApiParam("ФИО получателя")
    private String fio;

    @QueryParam("roleCodes")
    @ApiParam("Список кодов ролей")
    private List<String> roleCodes;

    @QueryParam("regionId")
    @ApiParam("Идентификатор региона")
    private Integer regionId;

    @QueryParam("organizationId")
    @ApiParam("Идентификатор организации")
    private Integer organizationId;
}
