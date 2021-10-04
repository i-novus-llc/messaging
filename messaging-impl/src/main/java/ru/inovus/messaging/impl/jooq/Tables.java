/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;

import ru.inovus.messaging.impl.jooq.tables.*;


/**
 * Convenience access to all tables in messaging
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * Каналы отправки уведомлений
     */
    public static final Channel CHANNEL = Channel.CHANNEL;

    /**
     * Уведомления
     */
    public static final Message MESSAGE = Message.MESSAGE;

    /**
     * Получатель уведомления
     */
    public static final MessageRecipient MESSAGE_RECIPIENT = MessageRecipient.MESSAGE_RECIPIENT;

    /**
     * Шаблоны уведомлений (общесистемные настройки)
     */
    public static final MessageSetting MESSAGE_SETTING = MessageSetting.MESSAGE_SETTING;

    /**
     * Тенанты
     */
    public static final Tenant TENANT = Tenant.TENANT;

    /**
     * Пользовательские настройки уведомлений
     */
    public static final UserSetting USER_SETTING = UserSetting.USER_SETTING;
}
