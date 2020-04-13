package ru.inovus.messaging.api.criteria;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.platform.jaxrs.RestCriteria;

import javax.ws.rs.QueryParam;
import java.util.UUID;

@Getter
@Setter
public class RecipientCriteria extends RestCriteria {

    @QueryParam("messageId")
    private UUID messageId;
}
