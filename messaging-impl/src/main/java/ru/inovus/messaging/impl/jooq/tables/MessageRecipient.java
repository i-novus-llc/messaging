/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;

import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecipientRecord;
import ru.inovus.messaging.impl.util.MessageStatusTypeConverter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Получатель уведомления
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessageRecipient extends TableImpl<MessageRecipientRecord> {

    private static final long serialVersionUID = -1996330404;

    /**
     * The reference instance of <code>messaging.message_recipient</code>
     */
    public static final MessageRecipient MESSAGE_RECIPIENT = new MessageRecipient();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MessageRecipientRecord> getRecordType() {
        return MessageRecipientRecord.class;
    }

    /**
     * The column <code>messaging.message_recipient.id</code>. Уникальный идентификатор
     */
    public final TableField<MessageRecipientRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>messaging.message_recipient.message_id</code>. Идентификатор уведомления
     */
    public final TableField<MessageRecipientRecord, UUID> MESSAGE_ID = createField(DSL.name("message_id"), org.jooq.impl.SQLDataType.UUID, this, "Идентификатор уведомления");

    /**
     * The column <code>messaging.message_recipient.status_time</code>. Время установки статуса
     */
    public final TableField<MessageRecipientRecord, LocalDateTime> STATUS_TIME = createField(DSL.name("status_time"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "Время установки статуса");

    /**
     * The column <code>messaging.message_recipient.recipient_name</code>. Имя контакта получателя
     */
    public final TableField<MessageRecipientRecord, String> RECIPIENT_NAME = createField(DSL.name("recipient_name"), org.jooq.impl.SQLDataType.VARCHAR, this, "Имя контакта получателя");

    /**
     * The column <code>messaging.message_recipient.recipient_send_channel_id</code>. Идентификатор получателя в формате канала доставки
     */
    public final TableField<MessageRecipientRecord, String> RECIPIENT_SEND_CHANNEL_ID = createField(DSL.name("recipient_send_channel_id"), org.jooq.impl.SQLDataType.VARCHAR, this, "Идентификатор получателя в формате канала доставки");

    /**
     * The column <code>messaging.message_recipient.status</code>. Текущий статус отправки уведомления получателю
     */
    public final TableField<MessageRecipientRecord, MessageStatusType> STATUS = createField(DSL.name("status"), org.jooq.impl.SQLDataType.VARCHAR, this, "Текущий статус отправки уведомления получателю", new MessageStatusTypeConverter());

    /**
     * The column <code>messaging.message_recipient.departured_at</code>. Дата и время фактической отправки уведомления
     */
    public final TableField<MessageRecipientRecord, LocalDateTime> DEPARTURED_AT = createField(DSL.name("departured_at"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "Дата и время фактической отправки уведомления");

    /**
     * The column <code>messaging.message_recipient.send_message_error</code>. Сообщение ошибки отправки уведомления
     */
    public final TableField<MessageRecipientRecord, String> SEND_MESSAGE_ERROR = createField(DSL.name("send_message_error"), org.jooq.impl.SQLDataType.VARCHAR, this, "Сообщение ошибки отправки уведомления");

    /**
     * Create a <code>messaging.message_recipient</code> table reference
     */
    public MessageRecipient() {
        this(DSL.name("message_recipient"), null);
    }

    /**
     * Create an aliased <code>messaging.message_recipient</code> table reference
     */
    public MessageRecipient(String alias) {
        this(DSL.name(alias), MESSAGE_RECIPIENT);
    }

    /**
     * Create an aliased <code>messaging.message_recipient</code> table reference
     */
    public MessageRecipient(Name alias) {
        this(alias, MESSAGE_RECIPIENT);
    }

    private MessageRecipient(Name alias, Table<MessageRecipientRecord> aliased) {
        this(alias, aliased, null);
    }

    private MessageRecipient(Name alias, Table<MessageRecipientRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Получатель уведомления"), TableOptions.table());
    }

    public <O extends Record> MessageRecipient(Table<O> child, ForeignKey<O, MessageRecipientRecord> key) {
        super(child, key, MESSAGE_RECIPIENT);
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public UniqueKey<MessageRecipientRecord> getPrimaryKey() {
        return Keys.RECIPIENT_PKEY;
    }

    @Override
    public List<UniqueKey<MessageRecipientRecord>> getKeys() {
        return Arrays.<UniqueKey<MessageRecipientRecord>>asList(Keys.RECIPIENT_PKEY);
    }

    @Override
    public List<ForeignKey<MessageRecipientRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MessageRecipientRecord, ?>>asList(Keys.MESSAGE_RECIPIENT__RECIPIENT_MESSAGE_ID_FKEY);
    }

    public Message message() {
        return new Message(this, Keys.MESSAGE_RECIPIENT__RECIPIENT_MESSAGE_ID_FKEY);
    }

    @Override
    public MessageRecipient as(String alias) {
        return new MessageRecipient(DSL.name(alias), this);
    }

    @Override
    public MessageRecipient as(Name alias) {
        return new MessageRecipient(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MessageRecipient rename(String name) {
        return new MessageRecipient(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MessageRecipient rename(Name name) {
        return new MessageRecipient(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Integer, UUID, LocalDateTime, String, String, MessageStatusType, LocalDateTime, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
