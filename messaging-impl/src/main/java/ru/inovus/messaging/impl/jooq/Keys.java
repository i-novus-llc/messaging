/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq;


import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

import ru.inovus.messaging.impl.jooq.tables.Channel;
import ru.inovus.messaging.impl.jooq.tables.Message;
import ru.inovus.messaging.impl.jooq.tables.MessageRecipient;
import ru.inovus.messaging.impl.jooq.tables.MessageTemplate;
import ru.inovus.messaging.impl.jooq.tables.Tenant;
import ru.inovus.messaging.impl.jooq.tables.records.ChannelRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.impl.jooq.tables.records.MessageTemplateRecord;
import ru.inovus.messaging.impl.jooq.tables.records.TenantRecord;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>messaging</code> schema.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<ChannelRecord, Integer> IDENTITY_CHANNEL = Identities0.IDENTITY_CHANNEL;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<ChannelRecord> CHANNEL_PKEY = UniqueKeys0.CHANNEL_PKEY;
    public static final UniqueKey<MessageRecord> MESSAGE_PKEY = UniqueKeys0.MESSAGE_PKEY;
    public static final UniqueKey<MessageRecipientRecord> MESSAGE_RECIPIENT_PKEY = UniqueKeys0.MESSAGE_RECIPIENT_PKEY;
    public static final UniqueKey<MessageTemplateRecord> MESSAGE_TEMPLATE_PKEY = UniqueKeys0.MESSAGE_TEMPLATE_PKEY;
    public static final UniqueKey<MessageTemplateRecord> MESSAGE_TEMPLATE_CODE_KEY = UniqueKeys0.MESSAGE_TEMPLATE_CODE_KEY;
    public static final UniqueKey<TenantRecord> TENANT_PKEY = UniqueKeys0.TENANT_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<ChannelRecord, TenantRecord> CHANNEL__CHANNEL_TENANT_CODE_FKEY = ForeignKeys0.CHANNEL__CHANNEL_TENANT_CODE_FKEY;
    public static final ForeignKey<MessageRecord, TenantRecord> MESSAGE__MESSAGE_TENANT_CODE_FKEY = ForeignKeys0.MESSAGE__MESSAGE_TENANT_CODE_FKEY;
    public static final ForeignKey<MessageRecord, ChannelRecord> MESSAGE__MESSAGE_CHANNEL_ID_CHANNEL_ID_FK = ForeignKeys0.MESSAGE__MESSAGE_CHANNEL_ID_CHANNEL_ID_FK;
    public static final ForeignKey<MessageRecipientRecord, MessageRecord> MESSAGE_RECIPIENT__MESSAGE_RECIPIENT_MESSAGE_ID_FKEY = ForeignKeys0.MESSAGE_RECIPIENT__MESSAGE_RECIPIENT_MESSAGE_ID_FKEY;
    public static final ForeignKey<MessageTemplateRecord, ChannelRecord> MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_CHANNEL_ID_CHANNEL_ID_FK = ForeignKeys0.MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_CHANNEL_ID_CHANNEL_ID_FK;
    public static final ForeignKey<MessageTemplateRecord, TenantRecord> MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_TENANT_CODE_FKEY = ForeignKeys0.MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_TENANT_CODE_FKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<ChannelRecord, Integer> IDENTITY_CHANNEL = Internal.createIdentity(Channel.CHANNEL, Channel.CHANNEL.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<ChannelRecord> CHANNEL_PKEY = Internal.createUniqueKey(Channel.CHANNEL, "channel_pkey", new TableField[] { Channel.CHANNEL.ID }, true);
        public static final UniqueKey<MessageRecord> MESSAGE_PKEY = Internal.createUniqueKey(Message.MESSAGE, "message_pkey", new TableField[] { Message.MESSAGE.ID }, true);
        public static final UniqueKey<MessageRecipientRecord> MESSAGE_RECIPIENT_PKEY = Internal.createUniqueKey(MessageRecipient.MESSAGE_RECIPIENT, "message_recipient_pkey", new TableField[] { MessageRecipient.MESSAGE_RECIPIENT.ID }, true);
        public static final UniqueKey<MessageTemplateRecord> MESSAGE_TEMPLATE_PKEY = Internal.createUniqueKey(MessageTemplate.MESSAGE_TEMPLATE, "message_template_pkey", new TableField[] { MessageTemplate.MESSAGE_TEMPLATE.ID }, true);
        public static final UniqueKey<MessageTemplateRecord> MESSAGE_TEMPLATE_CODE_KEY = Internal.createUniqueKey(MessageTemplate.MESSAGE_TEMPLATE, "message_template_code_key", new TableField[] { MessageTemplate.MESSAGE_TEMPLATE.CODE }, true);
        public static final UniqueKey<TenantRecord> TENANT_PKEY = Internal.createUniqueKey(Tenant.TENANT, "tenant_pkey", new TableField[] { Tenant.TENANT.CODE }, true);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<ChannelRecord, TenantRecord> CHANNEL__CHANNEL_TENANT_CODE_FKEY = Internal.createForeignKey(Keys.TENANT_PKEY, Channel.CHANNEL, "channel_tenant_code_fkey", new TableField[] { Channel.CHANNEL.TENANT_CODE }, true);
        public static final ForeignKey<MessageRecord, TenantRecord> MESSAGE__MESSAGE_TENANT_CODE_FKEY = Internal.createForeignKey(Keys.TENANT_PKEY, Message.MESSAGE, "message_tenant_code_fkey", new TableField[] { Message.MESSAGE.TENANT_CODE }, true);
        public static final ForeignKey<MessageRecord, ChannelRecord> MESSAGE__MESSAGE_CHANNEL_ID_CHANNEL_ID_FK = Internal.createForeignKey(Keys.CHANNEL_PKEY, Message.MESSAGE, "message_channel_id_channel_id_fk", new TableField[] { Message.MESSAGE.CHANNEL_ID }, true);
        public static final ForeignKey<MessageRecipientRecord, MessageRecord> MESSAGE_RECIPIENT__MESSAGE_RECIPIENT_MESSAGE_ID_FKEY = Internal.createForeignKey(Keys.MESSAGE_PKEY, MessageRecipient.MESSAGE_RECIPIENT, "message_recipient_message_id_fkey", new TableField[] { MessageRecipient.MESSAGE_RECIPIENT.MESSAGE_ID }, true);
        public static final ForeignKey<MessageTemplateRecord, ChannelRecord> MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_CHANNEL_ID_CHANNEL_ID_FK = Internal.createForeignKey(Keys.CHANNEL_PKEY, MessageTemplate.MESSAGE_TEMPLATE, "message_template_channel_id_channel_id_fk", new TableField[] { MessageTemplate.MESSAGE_TEMPLATE.CHANNEL_ID }, true);
        public static final ForeignKey<MessageTemplateRecord, TenantRecord> MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_TENANT_CODE_FKEY = Internal.createForeignKey(Keys.TENANT_PKEY, MessageTemplate.MESSAGE_TEMPLATE, "message_template_tenant_code_fkey", new TableField[] { MessageTemplate.MESSAGE_TEMPLATE.TENANT_CODE }, true);
    }
}
