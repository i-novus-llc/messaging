/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.MessageTemplateRecord;
import ru.inovus.messaging.impl.util.AlertTypeConverter;
import ru.inovus.messaging.impl.util.SeverityConverter;

import java.util.Arrays;
import java.util.List;


/**
 * Шаблоны уведомлений
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessageTemplate extends TableImpl<MessageTemplateRecord> {

    private static final long serialVersionUID = 378279949;

    /**
     * The reference instance of <code>messaging.message_template</code>
     */
    public static final MessageTemplate MESSAGE_TEMPLATE = new MessageTemplate();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MessageTemplateRecord> getRecordType() {
        return MessageTemplateRecord.class;
    }

    /**
     * The column <code>messaging.message_template.id</code>. Уникальный идентификатор
     */
    public final TableField<MessageTemplateRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>messaging.message_template.caption</code>. Заголовок уведомления
     */
    public final TableField<MessageTemplateRecord, String> CAPTION = createField(DSL.name("caption"), org.jooq.impl.SQLDataType.VARCHAR, this, "Заголовок уведомления");

    /**
     * The column <code>messaging.message_template.text</code>. Содержимое уведомления
     */
    public final TableField<MessageTemplateRecord, String> TEXT = createField(DSL.name("text"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Содержимое уведомления");

    /**
     * The column <code>messaging.message_template.severity</code>. Важность уведомления
     */
    public final TableField<MessageTemplateRecord, Severity> SEVERITY = createField(DSL.name("severity"), org.jooq.impl.SQLDataType.VARCHAR, this, "Важность уведомления", new SeverityConverter());

    /**
     * The column <code>messaging.message_template.alert_type</code>. Способ отображения уведомления
     */
    public final TableField<MessageTemplateRecord, AlertType> ALERT_TYPE = createField(DSL.name("alert_type"), org.jooq.impl.SQLDataType.VARCHAR, this, "Способ отображения уведомления", new AlertTypeConverter());

    /**
     * The column <code>messaging.message_template.name</code>. Наименование шаблона уведомления
     */
    public final TableField<MessageTemplateRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR, this, "Наименование шаблона уведомления");

    /**
     * The column <code>messaging.message_template.enabled</code>. Признак включения уведомления
     */
    public final TableField<MessageTemplateRecord, Boolean> ENABLED = createField(DSL.name("enabled"), org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "Признак включения уведомления");

    /**
     * The column <code>messaging.message_template.code</code>. Код шаблона уведомления
     */
    public final TableField<MessageTemplateRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Код шаблона уведомления");

    /**
     * The column <code>messaging.message_template.channel_code</code>. Код канала отправки уведомления
     */
    public final TableField<MessageTemplateRecord, String> CHANNEL_CODE = createField(DSL.name("channel_code"), org.jooq.impl.SQLDataType.VARCHAR, this, "Код канала отправки уведомления");

    /**
     * The column <code>messaging.message_template.tenant_code</code>. Тенант, к которому относится настройка
     */
    public final TableField<MessageTemplateRecord, String> TENANT_CODE = createField(DSL.name("tenant_code"), org.jooq.impl.SQLDataType.VARCHAR, this, "Тенант, к которому относится настройка");

    /**
     * Create a <code>messaging.message_template</code> table reference
     */
    public MessageTemplate() {
        this(DSL.name("message_template"), null);
    }

    /**
     * Create an aliased <code>messaging.message_template</code> table reference
     */
    public MessageTemplate(String alias) {
        this(DSL.name(alias), MESSAGE_TEMPLATE);
    }

    /**
     * Create an aliased <code>messaging.message_template</code> table reference
     */
    public MessageTemplate(Name alias) {
        this(alias, MESSAGE_TEMPLATE);
    }

    private MessageTemplate(Name alias, Table<MessageTemplateRecord> aliased) {
        this(alias, aliased, null);
    }

    private MessageTemplate(Name alias, Table<MessageTemplateRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Шаблоны уведомлений"), TableOptions.table());
    }

    public <O extends Record> MessageTemplate(Table<O> child, ForeignKey<O, MessageTemplateRecord> key) {
        super(child, key, MESSAGE_TEMPLATE);
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CODE_UX, Indexes.MESSAGE_TEMPLATE_TENANT_CODE_IDX);
    }

    @Override
    public UniqueKey<MessageTemplateRecord> getPrimaryKey() {
        return Keys.MESSAGE_TEMPLATE_PKEY;
    }

    @Override
    public List<UniqueKey<MessageTemplateRecord>> getKeys() {
        return Arrays.<UniqueKey<MessageTemplateRecord>>asList(Keys.MESSAGE_TEMPLATE_PKEY, Keys.MESSAGE_TEMPLATE_CODE_KEY);
    }

    @Override
    public List<ForeignKey<MessageTemplateRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MessageTemplateRecord, ?>>asList(Keys.MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_CHANNEL_CODE_CHANNEL_CODE_FK, Keys.MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_TENANT_CODE_FKEY);
    }

    public Channel channel() {
        return new Channel(this, Keys.MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_CHANNEL_CODE_CHANNEL_CODE_FK);
    }

    public Tenant tenant() {
        return new Tenant(this, Keys.MESSAGE_TEMPLATE__MESSAGE_TEMPLATE_TENANT_CODE_FKEY);
    }

    @Override
    public MessageTemplate as(String alias) {
        return new MessageTemplate(DSL.name(alias), this);
    }

    @Override
    public MessageTemplate as(Name alias) {
        return new MessageTemplate(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MessageTemplate rename(String name) {
        return new MessageTemplate(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MessageTemplate rename(Name name) {
        return new MessageTemplate(name, null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<Integer, String, String, Severity, AlertType, String, Boolean, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }
}
