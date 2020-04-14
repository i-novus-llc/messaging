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
package ru.inovus.messaging.api.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.data.domain.Page;
import ru.inovus.messaging.api.criteria.ComponentCriteria;
import ru.inovus.messaging.api.model.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api("Компоненты системы")
@Path("/components")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ComponentRest {

    @GET
    @ApiOperation("Получение списка компонентов системы")
    @ApiResponse(code = 200, message = "Страница компонентов системы")
    Page<Component> getComponents(@BeanParam ComponentCriteria criteria);
}
