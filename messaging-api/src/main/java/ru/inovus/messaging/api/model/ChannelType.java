package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ApiModel("Тип канала отправки уведомлений")
public class ChannelType implements Serializable {
    @ApiModelProperty("Идентификатор канала")
    private String id;
    @ApiModelProperty("Наименование канала")
    private String name;
}
