/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row16;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.*;

import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.RecipientType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Public;
import ru.inovus.messaging.impl.jooq.tables.records.MessageRecord;
import ru.inovus.messaging.impl.util.AlertTypeConverter;
import ru.inovus.messaging.impl.util.FormationTypeConverter;
import ru.inovus.messaging.impl.util.RecipientTypeConverter;
import ru.inovus.messaging.impl.util.SeverityConverter;


/**
 * Сообщения
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Message extends TableImpl<MessageRecord> {

    private static final long serialVersionUID = -1382108795;

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
    public final TableField<MessageRecord, UUID> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.UUID.nullable(false).defaultValue(org.jooq.impl.DSL.field("uuid_generate_v4()", org.jooq.impl.SQLDataType.UUID)), this, "Уникальный идентификатор");

    /**
     * The column <code>public.message.caption</code>. Заголовок
     */
    public final TableField<MessageRecord, String> CAPTION = createField(DSL.name("caption"), org.jooq.impl.SQLDataType.VARCHAR, this, "Заголовок");

    /**
     * The column <code>public.message.text</code>. Содержимое сообщения
     */
    public final TableField<MessageRecord, String> TEXT = createField(DSL.name("text"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Содержимое сообщения");

    /**
     * The column <code>public.message.severity</code>. Жесткость сообщения
     */
    public final TableField<MessageRecord, Severity> SEVERITY = createField(DSL.name("severity"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Жесткость сообщения", new SeverityConverter());

    /**
     * The column <code>public.message.alert_type</code>. Тип предупреждения
     */
    public final TableField<MessageRecord, AlertType> ALERT_TYPE = createField(DSL.name("alert_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Тип предупреждения", new AlertTypeConverter());

    /**
     * The column <code>public.message.sent_at</code>. Отправлено (дата и время)
     */
    public final TableField<MessageRecord, LocalDateTime> SENT_AT = createField(DSL.name("sent_at"), SQLDataType.TIMESTAMP, this, "Отправлено (дата и время)", new TimestampToLocalDateTimeConverter());

    /**
     * The column <code>public.message.system_id</code>. Идентификатор системы
     */
    public final TableField<MessageRecord, String> SYSTEM_ID = createField(DSL.name("system_id"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Идентификатор системы");

    /**
     * The column <code>public.message.component_id</code>. Компонент Системы, к которому относится уведомление
     */
    public final TableField<MessageRecord, Integer> COMPONENT_ID = createField(DSL.name("component_id"), org.jooq.impl.SQLDataType.INTEGER, this, "Компонент Системы, к которому относится уведомление");

    /**
     * The column <code>public.message.formation_type</code>. Тип формирования уведомления
     */
    public final TableField<MessageRecord, FormationType> FORMATION_TYPE = createField(DSL.name("formation_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false).defaultValue(org.jooq.impl.DSL.field("'AUTO'::character varying", org.jooq.impl.SQLDataType.VARCHAR)), this, "Тип формирования уведомления", new FormationTypeConverter());

    /**
     * The column <code>public.message.recipient_type</code>.
     */
    public final TableField<MessageRecord, RecipientType> RECIPIENT_TYPE = createField(DSL.name("recipient_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "", new RecipientTypeConverter());

    /**
     * The column <code>public.message.notification_type</code>.
     */
    public final TableField<MessageRecord, String> NOTIFICATION_TYPE = createField(DSL.name("notification_type"), org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.message.object_id</code>.
     */
    public final TableField<MessageRecord, String> OBJECT_ID = createField(DSL.name("object_id"), org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.message.object_type</code>.
     */
    public final TableField<MessageRecord, String> OBJECT_TYPE = createField(DSL.name("object_type"), org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * The column <code>public.message.send_email_date</code>. Дата и время отправки email
     */
    public final TableField<MessageRecord, LocalDateTime> SEND_EMAIL_DATE = createField(DSL.name("send_email_date"), org.jooq.impl.SQLDataType.LOCALDATETIME, this, "Дата и время отправки email");

    /**
     * The column <code>public.message.send_email_error</code>. Ошибка, возникшая при последней попытке отправки email
     */
    public final TableField<MessageRecord, String> SEND_EMAIL_ERROR = createField(DSL.name("send_email_error"), org.jooq.impl.SQLDataType.VARCHAR, this, "Ошибка, возникшая при последней попытке отправки email");

    /**
     * The column <code>public.message.channel_id</code>. Идентификатор канала отправки
     */
    public final TableField<MessageRecord, String> CHANNEL_ID = createField(DSL.name("channel_id"), org.jooq.impl.SQLDataType.VARCHAR, this, "Идентификатор канала отправки");

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
        super(alias, null, aliased, parameters, DSL.comment("Сообщения"), TableOptions.table());
    }

    public <O extends Record> Message(Table<O> child, ForeignKey<O, MessageRecord> key) {
        super(child, key, MESSAGE);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
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
    public List<Check<MessageRecord>> getChecks() {
        return Arrays.<Check<MessageRecord>>asList(
              Internal.createCheck(this, DSL.name("message_alert_type_check"), "(((alert_type)::text = ANY ((ARRAY['BLOCKER'::character varying, 'POPUP'::character varying, 'HIDDEN'::character varying])::text[])))", true)
            , Internal.createCheck(this, DSL.name("message_formation_type_check"), "(((formation_type)::text = ANY ((ARRAY['AUTO'::character varying, 'HAND'::character varying])::text[])))", true)
            , Internal.createCheck(this, DSL.name("message_recipient_type_check"), "(((recipient_type)::text = ANY ((ARRAY['ALL'::character varying, 'USER'::character varying])::text[])))", true)
            , Internal.createCheck(this, DSL.name("message_severity_check"), "(((severity)::text = ANY (ARRAY[('40'::character varying)::text, ('30'::character varying)::text, ('20'::character varying)::text, ('10'::character varying)::text])))", true)
        );
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
    // Row16 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row16<UUID, String, String, Severity, AlertType, LocalDateTime, String, Integer, FormationType, RecipientType, String, String, String, LocalDateTime, String, String> fieldsRow() {
        return (Row16) super.fieldsRow();
    }
}
