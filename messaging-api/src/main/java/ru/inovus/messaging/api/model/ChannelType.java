package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@ApiModel("Тип канала отправки уведомлений")
public class ChannelType {
    @ApiModelProperty("Код канала")
    private String code;
    @ApiModelProperty("Наименование канала")
    private String name;
}
