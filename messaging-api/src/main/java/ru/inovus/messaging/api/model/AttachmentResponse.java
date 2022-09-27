package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ApiModel("Файл")
public class AttachmentResponse implements Serializable {

    @ApiModelProperty("Идентификатор файла")
    private UUID id;

    @ApiModelProperty("Название файла в хранилище")
    private String fileName;

    @ApiModelProperty("Название файла для UI")
    private String shortFileName;
}
