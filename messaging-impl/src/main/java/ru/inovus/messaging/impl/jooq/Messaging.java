/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;


import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import ru.inovus.messaging.impl.jooq.tables.*;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Messaging extends SchemaImpl {

    private static final long serialVersionUID = 1648689555;

    /**
     * The reference instance of <code>messaging</code>
     */
    public static final Messaging MESSAGING = new Messaging();

    /**
     * Каналы отправки уведомлений
     */
    public final Channel CHANNEL = Channel.CHANNEL;

    /**
     * Уведомления
     */
    public final Message MESSAGE = Message.MESSAGE;

    /**
     * Получатель уведомления
     */
    public final MessageRecipient MESSAGE_RECIPIENT = MessageRecipient.MESSAGE_RECIPIENT;

    /**
     * Шаблоны уведомлений (общесистемные настройки)
     */
    public final MessageSetting MESSAGE_SETTING = MessageSetting.MESSAGE_SETTING;

    /**
     * Тенанты
     */
    public final Tenant TENANT = Tenant.TENANT;

    /**
     * No further instances allowed
     */
    private Messaging() {
        super("messaging", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.<Sequence<?>>asList(
            Sequences.CHANNEL_ID_SEQ,
            Sequences.MESSAGE_ID_SEQ,
            Sequences.MESSAGE_SETTING_ID_SEQ,
            Sequences.RECIPIENT_ID_SEQ,
            Sequences.USER_SETTING_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Channel.CHANNEL,
            Message.MESSAGE,
            MessageRecipient.MESSAGE_RECIPIENT,
            MessageSetting.MESSAGE_SETTING,
            Tenant.TENANT);
    }
}
