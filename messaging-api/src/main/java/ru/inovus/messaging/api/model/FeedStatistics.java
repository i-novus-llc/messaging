package ru.inovus.messaging.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FeedStatistics {

    @ApiModelProperty("Общее количество уведомлений")
    private int total;

    @ApiModelProperty("Количество прочитанных уведомлений")
    private int read;

    @ApiModelProperty("Количество непрочитанных уведомлений (статус 'Отправлено')")
    private int unread;

    @ApiModelProperty("Количество пользовательских уведомлений")
    private int user;

    @ApiModelProperty("Количество системных уведомлений")
    private int system;

    @ApiModelProperty("Количество уведомлений с типом получателя 'Пользователь'")
    private int recipient;

    @ApiModelProperty("Количество уведомлений с типом получателя 'Пользователи, отфильтрованные по роли'")
    private int userGroupByRole;
}
