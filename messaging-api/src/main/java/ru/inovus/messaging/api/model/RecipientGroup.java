package ru.inovus.messaging.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipientGroup implements Serializable {

    @ApiModelProperty("Идентификатор уведомления")
    private Integer id;

    @ApiModelProperty("Название группы получателей")
    private String name;

    @ApiModelProperty("Описание группы получателей")
    private String description;

    @JsonIgnore
    private String tenantCode;

    @ApiModelProperty("Список получателей")
    private List<Recipient> recipients;

    @ApiModelProperty("Коды шаблонов уведомлений")
    private List<MessageTemplate> templates;

}
