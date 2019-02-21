/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;


import ru.inovus.messaging.impl.jooq.tables.*;

import javax.annotation.Generated;


/**
 * Convenience access to all tables in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * Компоненты системы
     */
    public static final Component COMPONENT = ru.inovus.messaging.impl.jooq.tables.Component.COMPONENT;

    /**
     * Сообщения
     */
    public static final Message MESSAGE = ru.inovus.messaging.impl.jooq.tables.Message.MESSAGE;

    /**
     * Шаблоны уведомлений (общесистемные настройки)
     */
    public static final MessageSetting MESSAGE_SETTING = ru.inovus.messaging.impl.jooq.tables.MessageSetting.MESSAGE_SETTING;

    /**
     * Получатели сообщения
     */
    public static final Recipient RECIPIENT = ru.inovus.messaging.impl.jooq.tables.Recipient.RECIPIENT;

    /**
     * Пользовательские настройки уведомлений
     */
    public static final UserSetting USER_SETTING = ru.inovus.messaging.impl.jooq.tables.UserSetting.USER_SETTING;
}
