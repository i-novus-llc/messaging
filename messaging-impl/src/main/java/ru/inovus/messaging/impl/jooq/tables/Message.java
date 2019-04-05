/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TimestampToLocalDateTimeConverter;

import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.api.model.FormationType;
import ru.inovus.messaging.api.model.RecipientType;
import ru.inovus.messaging.api.model.Severity;
import ru.inovus.messaging.impl.AlertTypeConverter;
import ru.inovus.messaging.impl.FormationTypeConverter;
import ru.inovus.messaging.impl.RecipientTypeConverter;
import ru.inovus.messaging.impl.SeverityConverter;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Public;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;


/**
 * Сообщения
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Message extends TableImpl<MessageRecord> {

    private static final long serialVersionUID = -435450934;

    /**
     * The reference instance of <code>public.message</code>
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
     * The column <code>public.message.id</code>. Уникальный идентификатор
     */
    public final TableField<MessageRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>public.message.caption</code>. Заголовок
     */
    public final TableField<MessageRecord, String> CAPTION = createField("caption", org.jooq.impl.SQLDataType.VARCHAR, this, "Заголовок");

    /**
     * The column <code>public.message.text</code>. Содержимое сообщения
     */
    public final TableField<MessageRecord, String> TEXT = createField("text", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Содержимое сообщения");

    /**
     * The column <code>public.message.severity</code>. Жесткость сообщения
     */
    public final TableField<MessageRecord, Severity> SEVERITY = createField("severity", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Жесткость сообщения", new SeverityConverter());

    /**
     * The column <code>public.message.alert_type</code>. Тип предупреждения
     */
    public final TableField<MessageRecord, AlertType> ALERT_TYPE = createField("alert_type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Тип предупреждения", new AlertTypeConverter());

    /**
     * The column <code>public.message.sent_at</code>. Отправлено (дата и время)
     */
    public final TableField<MessageRecord, LocalDateTime> SENT_AT = createField("sent_at", org.jooq.impl.SQLDataType.TIMESTAMP, this, "Отправлено (дата и время)", new TimestampToLocalDateTimeConverter());

    /**
     * The column <code>public.message.system_id</code>. Идентификатор системы
     */
    public final TableField<MessageRecord, String> SYSTEM_ID = createField("system_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Идентификатор системы");

    /**
     * The column <code>public.message.component_id</code>. Компонент Системы, к которому относится уведомление
     */
    public final TableField<MessageRecord, Integer> COMPONENT_ID = createField("component_id", org.jooq.impl.SQLDataType.INTEGER, this, "Компонент Системы, к которому относится уведомление");

    /**
     * The column <code>public.message.formation_type</code>. Тип формирования уведомления
     */
    public final TableField<MessageRecord, FormationType> FORMATION_TYPE = createField("formation_type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false).defaultValue(org.jooq.impl.DSL.field("'AUTO'::character varying", org.jooq.impl.SQLDataType.VARCHAR)), this, "Тип формирования уведомления", new FormationTypeConverter());

    /**
     * The column <code>public.message.recipient_type</code>.
     */
    public final TableField<MessageRecord, RecipientType> RECIPIENT_TYPE = createField("recipient_type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "", new RecipientTypeConverter());

    /**
     * The column <code>public.message.notification_type</code>.
     */
    public final TableField<MessageRecord, String> NOTIFICATION_TYPE = createField("notification_type", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.message.object_id</code>.
     */
    public final TableField<MessageRecord, String> OBJECT_ID = createField("object_id", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.message.object_type</code>.
     */
    public final TableField<MessageRecord, String> OBJECT_TYPE = createField("object_type", org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.message.send_notice</code>.
     */
    public final TableField<MessageRecord, Boolean> SEND_NOTICE = createField("send_notice", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.message.send_email</code>.
     */
    public final TableField<MessageRecord, Boolean> SEND_EMAIL = createField("send_email", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * Create a <code>public.message</code> table reference
     */
    public Message() {
        this(DSL.name("message"), null);
    }

    /**
     * Create an aliased <code>public.message</code> table reference
     */
    public Message(String alias) {
        this(DSL.name(alias), MESSAGE);
    }

    /**
     * Create an aliased <code>public.message</code> table reference
     */
    public Message(Name alias) {
        this(alias, MESSAGE);
    }

    private Message(Name alias, Table<MessageRecord> aliased) {
        this(alias, aliased, null);
    }

    private Message(Name alias, Table<MessageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Сообщения"));
    }

    public <O extends Record> Message(Table<O> child, ForeignKey<O, MessageRecord> key) {
        super(child, key, MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IX_MESSAGE_SYSTEM_ID, Indexes.MESSAGE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MessageRecord> getPrimaryKey() {
        return Keys.MESSAGE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MessageRecord>> getKeys() {
        return Arrays.<UniqueKey<MessageRecord>>asList(Keys.MESSAGE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<MessageRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MessageRecord, ?>>asList(Keys.MESSAGE__MESSAGE_COMPONENT_ID_FKEY);
    }

    public Component component() {
        return new Component(this, Keys.MESSAGE__MESSAGE_COMPONENT_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message as(String alias) {
        return new Message(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
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
}
