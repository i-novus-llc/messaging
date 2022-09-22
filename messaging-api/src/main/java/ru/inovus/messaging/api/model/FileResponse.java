package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("Файл")
public class FileResponse {

    @ApiModelProperty("Идентификатор файла")
    private String id;

    @ApiModelProperty("Название файла")
    private String fileName;
//todo надоли?
//    @ApiModelProperty("Ссылка на файл")
//    private String URL;
}
