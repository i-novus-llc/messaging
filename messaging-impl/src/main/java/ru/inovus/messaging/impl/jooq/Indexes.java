/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;


import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;

import ru.inovus.messaging.impl.jooq.tables.Message;
import ru.inovus.messaging.impl.jooq.tables.MessageSetting;
import ru.inovus.messaging.impl.jooq.tables.UserSetting;


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
    public static final Index USER_SETTING_USER_ID_MSG_SETTING_ID_UX = Indexes0.USER_SETTING_USER_ID_MSG_SETTING_ID_UX;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index IX_MESSAGE_SYSTEM_ID = Internal.createIndex("ix_message_system_id", Message.MESSAGE, new OrderField[] { Message.MESSAGE.SYSTEM_ID }, false);
        public static Index CODE_UX = Internal.createIndex("code_ux", MessageSetting.MESSAGE_SETTING, new OrderField[] { MessageSetting.MESSAGE_SETTING.CODE }, true);
        public static Index USER_SETTING_USER_ID_MSG_SETTING_ID_UX = Internal.createIndex("user_setting_user_id_msg_setting_id_ux", UserSetting.USER_SETTING, new OrderField[] { UserSetting.USER_SETTING.USER_ID, UserSetting.USER_SETTING.MSG_SETTING_ID }, true);
    }
}
