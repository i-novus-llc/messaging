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

import lombok.Getter;
import lombok.Setter;
import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.api.model.Severity;
import ru.inovus.messaging.api.model.YesNo;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Getter
@Setter
public class UserSettingCriteria extends BaseMessagingCriteria {
    @PathParam("user")
    private String user;

    @QueryParam("component.id")
    private Integer componentId;

    @QueryParam("severity.id")
    private Severity severity;

    @QueryParam("alertType.id")
    private AlertType alertType;

    @QueryParam("infoType.id")
    private String channelId;

    @QueryParam("name")
    private String name;

    @QueryParam("templateCode")
    private String templateCode;

    @QueryParam("enabled.id")
    private YesNo enabled;

    public Boolean getEnabled() {
        return enabled != null ? enabled.getValue() : null;
    }
}
