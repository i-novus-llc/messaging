package ru.inovus.messaging.api.criteria;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.platform.jaxrs.RestCriteria;
import ru.inovus.messaging.api.model.InfoType;
import ru.inovus.messaging.api.model.Severity;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;

@Getter
@Setter
public class MessageCriteria extends RestCriteria {

    private static final long serialVersionUID = 7609048158169451956L;

    @QueryParam("systemId")
    private String systemId;
    @QueryParam("sentAtBegin")
    private LocalDateTime sentAtBegin;
    @QueryParam("sentAtEnd")
    private LocalDateTime sentAtEnd;
    @QueryParam("severity.id")
    private Severity severity;
    @QueryParam("infoType.id")
    private InfoType infoType;
    @QueryParam("component.id")
    private Integer componentId;

}
