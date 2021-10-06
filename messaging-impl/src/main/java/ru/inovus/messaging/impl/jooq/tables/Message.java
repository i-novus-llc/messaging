/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;

import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.impl.util.AlertTypeConverter;
import ru.inovus.messaging.impl.util.FormationTypeConverter;
import ru.inovus.messaging.impl.util.RecipientTypeConverter;
import ru.inovus.messaging.impl.util.SeverityConverter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Время установки статуса
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Message extends TableImpl<MessageRecord> {

    private static final long serialVersionUID = 1850730950;

    /**
     * The reference instance of <code>messaging.message</code>
     */
    public static final Message MESSAGE = new Message();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MessageRecord> getRecordType() {
        return MessageRecord.class;
    }

    /**
     * The column <code>messaging.message.id</code>. Уникальный идентификатор
     */
    public final TableField<MessageRecord, UUID> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v4()", org.jooq.impl.SQLDataType.UUID)), this, "Уникальный идентификатор");

    /**
     * The column <code>messaging.message.caption</code>. Заголовок уведомления
     */
    public final TableField<MessageRecord, String> CAPTION = createField(DSL.name("caption"), org.jooq.impl.SQLDataType.VARCHAR, this, "Заголовок уведомления");

    /**
     * The column <code>messaging.message.text</code>. Содержимое уведомления
     */
    public final TableField<MessageRecord, String> TEXT = createField(DSL.name("text"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Содержимое уведомления");

    /**
     * The column <code>messaging.message.severity</code>. Важность уведомления
     */
    public final TableField<MessageRecord, Severity> SEVERITY = createField(DSL.name("severity"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Важность уведомления", new SeverityConverter());

    /**
     * The column <code>messaging.message.alert_type</code>. Способ отображения уведомления
     */
    public final TableField<MessageRecord, AlertType> ALERT_TYPE = createField(DSL.name("alert_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Способ отображения уведомления", new AlertTypeConverter());

    /**
     * The column <code>messaging.message.sent_at</code>. Дата и время отправки уведомления
     */
    public final TableField<MessageRecord, LocalDateTime> SENT_AT = createField(DSL.name("sent_at"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "Дата и время отправки уведомления");

    /**
     * The column <code>messaging.message.system_id</code>. Идентификатор системы, к которой относится уведомление
     */
    public final TableField<MessageRecord, String> SYSTEM_ID = createField(DSL.name("system_id"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Идентификатор системы, к которой относится уведомление");

    /**
     * The column <code>messaging.message.component_id</code>. Идентификатор компонента (модуля, подсистемы), к которому относится уведомление
     */
    public final TableField<MessageRecord, Integer> COMPONENT_ID = createField(DSL.name("component_id"), org.jooq.impl.SQLDataType.INTEGER, this, "Идентификатор компонента (модуля, подсистемы), к которому относится уведомление");

    /**
     * The column <code>messaging.message.formation_type</code>. Тип формирования уведомления
     */
    public final TableField<MessageRecord, FormationType> FORMATION_TYPE = createField(DSL.name("formation_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false).defaultValue(org.jooq.impl.DSL.field("'AUTO'::character varying", org.jooq.impl.SQLDataType.VARCHAR)), this, "Тип формирования уведомления", new FormationTypeConverter());

    /**
     * The column <code>messaging.message.recipient_type</code>. Тип получателя уведомления
     */
    public final TableField<MessageRecord, RecipientType> RECIPIENT_TYPE = createField(DSL.name("recipient_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Тип получателя уведомления", new RecipientTypeConverter());

    /**
     * The column <code>messaging.message.notification_type</code>. Код шаблона, который был использован для формирования уведомления
     */
    public final TableField<MessageRecord, String> NOTIFICATION_TYPE = createField(DSL.name("notification_type"), org.jooq.impl.SQLDataType.VARCHAR, this, "Код шаблона, который был использован для формирования уведомления");

    /**
     * The column <code>messaging.message.object_id</code>. Идентификатор объекта, по которому было направлено уведомление
     */
    public final TableField<MessageRecord, String> OBJECT_ID = createField(DSL.name("object_id"), org.jooq.impl.SQLDataType.VARCHAR, this, "Идентификатор объекта, по которому было направлено уведомление");

    /**
     * The column <code>messaging.message.object_type</code>. Тип объекта, по которому было направлено уведомление
     */
    public final TableField<MessageRecord, String> OBJECT_TYPE = createField(DSL.name("object_type"), org.jooq.impl.SQLDataType.VARCHAR, this, "Тип объекта, по которому было направлено уведомление");

    /**
     * The column <code>messaging.message.channel_id</code>. Идентификатор канала отправки уведомления
     */
    public final TableField<MessageRecord, String> CHANNEL_ID = createField(DSL.name("channel_id"), org.jooq.impl.SQLDataType.VARCHAR, this, "Идентификатор канала отправки уведомления");

    /**
     * Create a <code>messaging.message</code> table reference
     */
    public Message() {
        this(DSL.name("message"), null);
    }

    /**
     * Create an aliased <code>messaging.message</code> table reference
     */
    public Message(String alias) {
        this(DSL.name(alias), MESSAGE);
    }

    /**
     * Create an aliased <code>messaging.message</code> table reference
     */
    public Message(Name alias) {
        this(alias, MESSAGE);
    }

    private Message(Name alias, Table<MessageRecord> aliased) {
        this(alias, aliased, null);
    }

    private Message(Name alias, Table<MessageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Время установки статуса"), TableOptions.table());
    }

    public <O extends Record> Message(Table<O> child, ForeignKey<O, MessageRecord> key) {
        super(child, key, MESSAGE);
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IX_MESSAGE_SYSTEM_ID);
    }

    @Override
    public UniqueKey<MessageRecord> getPrimaryKey() {
        return Keys.MESSAGE_PKEY;
    }

    @Override
    public List<UniqueKey<MessageRecord>> getKeys() {
        return Arrays.<UniqueKey<MessageRecord>>asList(Keys.MESSAGE_PKEY);
    }

    @Override
    public List<ForeignKey<MessageRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MessageRecord, ?>>asList(Keys.MESSAGE__MESSAGE_COMPONENT_ID_FKEY, Keys.MESSAGE__MESSAGE_CHANNEL_ID_CHANNEL_ID_FK);
    }

    public Component component() {
        return new Component(this, Keys.MESSAGE__MESSAGE_COMPONENT_ID_FKEY);
    }

    public Channel channel() {
        return new Channel(this, Keys.MESSAGE__MESSAGE_CHANNEL_ID_CHANNEL_ID_FK);
    }

    @Override
    public Message as(String alias) {
        return new Message(DSL.name(alias), this);
    }

    @Override
    public Message as(Name alias) {
        return new Message(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Message rename(String name) {
        return new Message(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Message rename(Name name) {
        return new Message(name, null);
    }

    // -------------------------------------------------------------------------
    // Row14 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row14<UUID, String, String, Severity, AlertType, LocalDateTime, String, Integer, FormationType, RecipientType, String, String, String, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }
}
