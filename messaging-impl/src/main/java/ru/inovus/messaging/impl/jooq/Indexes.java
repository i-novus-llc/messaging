/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;

import ru.inovus.messaging.impl.jooq.tables.Message;
import ru.inovus.messaging.impl.jooq.tables.MessageTemplate;


/**
 * A class modelling indexes of tables of the <code>messaging</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index IX_MESSAGE_SYSTEM_ID = Indexes0.IX_MESSAGE_SYSTEM_ID;
    public static final Index CODE_UX = Indexes0.CODE_UX;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index IX_MESSAGE_SYSTEM_ID = Internal.createIndex("ix_message_system_id", Message.MESSAGE, new OrderField[] { Message.MESSAGE.TENANT_CODE }, false);
        public static Index CODE_UX = Internal.createIndex("code_ux", MessageTemplate.MESSAGE_TEMPLATE, new OrderField[] { MessageTemplate.MESSAGE_TEMPLATE.CODE }, true);
    }
}
