package ru.inovus.messaging.api.criteria;

import io.swagger.annotations.ApiParam;
import javax.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RecipientGroupCriteria extends BaseMessagingCriteria {

    @QueryParam("name")
    @ApiParam("Название группы получателей")
    private String name;

    @QueryParam("code")
    @ApiParam("Код группы получателей")
    private String code;

    @QueryParam("recipientNames")
    @ApiParam("Имена пользователей группы получателей")
    private List<String> recipientNames;

    @QueryParam("templateCodes")
    @ApiParam("Коды шаблонов группы получателей")
    private List<String> templateCodes;
}
