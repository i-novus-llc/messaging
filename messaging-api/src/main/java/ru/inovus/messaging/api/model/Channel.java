package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Тип канала отправки уведомлений")
public class Channel implements Serializable {

    @ApiModelProperty("Идентификатор канала")
    private Integer id;

    @ApiModelProperty("Наименование канала")
    private String name;

    @ApiModelProperty("Наименование очереди канала")
    private String queueName;
}

