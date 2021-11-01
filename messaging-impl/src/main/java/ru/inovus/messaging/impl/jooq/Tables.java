/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;


import ru.inovus.messaging.impl.jooq.tables.Channel;
import ru.inovus.messaging.impl.jooq.tables.Message;
import ru.inovus.messaging.impl.jooq.tables.MessageRecipient;
import ru.inovus.messaging.impl.jooq.tables.MessageTemplate;
import ru.inovus.messaging.impl.jooq.tables.Tenant;


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
     * Шаблоны уведомлений
     */
    public static final MessageTemplate MESSAGE_TEMPLATE = MessageTemplate.MESSAGE_TEMPLATE;

    /**
     * Тенанты
     */
    public static final Tenant TENANT = Tenant.TENANT;
}
