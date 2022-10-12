package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
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

    @ApiModelProperty("Размер файла в байтах")
    private Integer fileSize;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachmentResponse that = (AttachmentResponse) o;
        return Objects.equals(shortFileName, that.shortFileName) && Objects.equals(fileSize, that.fileSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortFileName, fileSize);
    }
}
