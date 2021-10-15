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
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.MessageSettingRecord;
import ru.inovus.messaging.impl.util.AlertTypeConverter;
import ru.inovus.messaging.impl.util.FormationTypeConverter;
import ru.inovus.messaging.impl.util.SeverityConverter;

import java.util.Arrays;
import java.util.List;


/**
 * Шаблоны уведомлений (общесистемные настройки)
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessageSetting extends TableImpl<MessageSettingRecord> {

    private static final long serialVersionUID = 1071572689;

    /**
     * The reference instance of <code>messaging.message_setting</code>
     */
    public static final MessageSetting MESSAGE_SETTING = new MessageSetting();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MessageSettingRecord> getRecordType() {
        return MessageSettingRecord.class;
    }

    /**
     * The column <code>messaging.message_setting.id</code>. Уникальный идентификатор
     */
    public final TableField<MessageSettingRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>messaging.message_setting.caption</code>. Заголовок уведомления
     */
    public final TableField<MessageSettingRecord, String> CAPTION = createField(DSL.name("caption"), org.jooq.impl.SQLDataType.VARCHAR, this, "Заголовок уведомления");

    /**
     * The column <code>messaging.message_setting.text</code>. Содержимое уведомления
     */
    public final TableField<MessageSettingRecord, String> TEXT = createField(DSL.name("text"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Содержимое уведомления");

    /**
     * The column <code>messaging.message_setting.severity</code>. Важность уведомления
     */
    public final TableField<MessageSettingRecord, Severity> SEVERITY = createField(DSL.name("severity"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Важность уведомления", new SeverityConverter());

    /**
     * The column <code>messaging.message_setting.alert_type</code>. Способ отображения уведомления
     */
    public final TableField<MessageSettingRecord, AlertType> ALERT_TYPE = createField(DSL.name("alert_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Способ отображения уведомления", new AlertTypeConverter());

    /**
     * The column <code>messaging.message_setting.name</code>. Наименование шаблона уведомления
     */
    public final TableField<MessageSettingRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR, this, "Наименование шаблона уведомления");

    /**
     * The column <code>messaging.message_setting.is_disabled</code>. Признак выключения уведомления
     */
    public final TableField<MessageSettingRecord, Boolean> IS_DISABLED = createField(DSL.name("is_disabled"), org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "Признак выключения уведомления");

    /**
     * The column <code>messaging.message_setting.formation_type</code>. Тип формирования уведомления
     */
    public final TableField<MessageSettingRecord, FormationType> FORMATION_TYPE = createField(DSL.name("formation_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Тип формирования уведомления", new FormationTypeConverter());

    /**
     * The column <code>messaging.message_setting.code</code>. Код шаблона уведомления
     */
    public final TableField<MessageSettingRecord, String> CODE = createField(DSL.name("code"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Код шаблона уведомления");

    /**
     * The column <code>messaging.message_setting.channel_id</code>. Идентификатор канала отправки уведомления
     */
    public final TableField<MessageSettingRecord, Integer> CHANNEL_ID = createField(DSL.name("channel_id"), org.jooq.impl.SQLDataType.INTEGER, this, "Идентификатор канала отправки уведомления");

    /**
     * The column <code>messaging.message_setting.tenant_code</code>. Тенант, к которому относится настройка
     */
    public final TableField<MessageSettingRecord, String> TENANT_CODE = createField(DSL.name("tenant_code"), org.jooq.impl.SQLDataType.VARCHAR, this, "Тенант, к которому относится настройка");

    /**
     * Create a <code>messaging.message_setting</code> table reference
     */
    public MessageSetting() {
        this(DSL.name("message_setting"), null);
    }

    /**
     * Create an aliased <code>messaging.message_setting</code> table reference
     */
    public MessageSetting(String alias) {
        this(DSL.name(alias), MESSAGE_SETTING);
    }

    /**
     * Create an aliased <code>messaging.message_setting</code> table reference
     */
    public MessageSetting(Name alias) {
        this(alias, MESSAGE_SETTING);
    }

    private MessageSetting(Name alias, Table<MessageSettingRecord> aliased) {
        this(alias, aliased, null);
    }

    private MessageSetting(Name alias, Table<MessageSettingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Шаблоны уведомлений (общесистемные настройки)"), TableOptions.table());
    }

    public <O extends Record> MessageSetting(Table<O> child, ForeignKey<O, MessageSettingRecord> key) {
        super(child, key, MESSAGE_SETTING);
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.CODE_UX);
    }

    @Override
    public UniqueKey<MessageSettingRecord> getPrimaryKey() {
        return Keys.MESSAGE_SETTING_PKEY;
    }

    @Override
    public List<UniqueKey<MessageSettingRecord>> getKeys() {
        return Arrays.<UniqueKey<MessageSettingRecord>>asList(Keys.MESSAGE_SETTING_PKEY);
    }

    @Override
    public List<ForeignKey<MessageSettingRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<MessageSettingRecord, ?>>asList(Keys.MESSAGE_SETTING__MESSAGE_SETTING_CHANNEL_ID_CHANNEL_ID_FK, Keys.MESSAGE_SETTING__MESSAGE_SETTING_TENANT_CODE_FKEY);
    }

    public Channel channel() {
        return new Channel(this, Keys.MESSAGE_SETTING__MESSAGE_SETTING_CHANNEL_ID_CHANNEL_ID_FK);
    }

    public Tenant tenant() {
        return new Tenant(this, Keys.MESSAGE_SETTING__MESSAGE_SETTING_TENANT_CODE_FKEY);
    }

    @Override
    public MessageSetting as(String alias) {
        return new MessageSetting(DSL.name(alias), this);
    }

    @Override
    public MessageSetting as(Name alias) {
        return new MessageSetting(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MessageSetting rename(String name) {
        return new MessageSetting(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MessageSetting rename(Name name) {
        return new MessageSetting(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, String, String, Severity, AlertType, String, Boolean, FormationType, String, Integer, String> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
