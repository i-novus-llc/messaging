/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;

import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.UserSettingRecord;
import ru.inovus.messaging.impl.util.AlertTypeConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Пользовательские настройки уведомлений
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class UserSetting extends TableImpl<UserSettingRecord> {

    private static final long serialVersionUID = -1906269694;

    /**
     * The reference instance of <code>messaging.user_setting</code>
     */
    public static final UserSetting USER_SETTING = new UserSetting();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserSettingRecord> getRecordType() {
        return UserSettingRecord.class;
    }

    /**
     * The column <code>messaging.user_setting.id</code>. Уникальный идентификатор
     */
    public final TableField<UserSettingRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('messaging.user_setting_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "Уникальный идентификатор");

    /**
     * The column <code>messaging.user_setting.alert_type</code>. Способ отображения уведомления
     */
    public final TableField<UserSettingRecord, AlertType> ALERT_TYPE = createField(DSL.name("alert_type"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Способ отображения уведомления", new AlertTypeConverter());

    /**
     * The column <code>messaging.user_setting.is_disabled</code>. Признак выключения уведомления
     */
    public final TableField<UserSettingRecord, Boolean> IS_DISABLED = createField(DSL.name("is_disabled"), org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "Признак выключения уведомления");

    /**
     * The column <code>messaging.user_setting.user_id</code>. Идентификатор пользователя, к которому относится настройка
     */
    public final TableField<UserSettingRecord, String> USER_ID = createField(DSL.name("user_id"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Идентификатор пользователя, к которому относится настройка");

    /**
     * The column <code>messaging.user_setting.msg_setting_id</code>. Идентификатор шаблона уведомления
     */
    public final TableField<UserSettingRecord, Integer> MSG_SETTING_ID = createField(DSL.name("msg_setting_id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Идентификатор шаблона уведомления");

    /**
     * The column <code>messaging.user_setting.channel_id</code>. Идентификатор канала отправки уведомления
     */
    public final TableField<UserSettingRecord, String> CHANNEL_ID = createField(DSL.name("channel_id"), org.jooq.impl.SQLDataType.VARCHAR, this, "Идентификатор канала отправки уведомления");

    /**
     * The column <code>messaging.user_setting.tenant_code</code>. Тенант, к которому относится пользовательская настройка
     */
    public final TableField<UserSettingRecord, String> TENANT_CODE = createField(DSL.name("tenant_code"), org.jooq.impl.SQLDataType.VARCHAR, this, "Тенант, к которому относится пользовательская настройка");

    /**
     * Create a <code>messaging.user_setting</code> table reference
     */
    public UserSetting() {
        this(DSL.name("user_setting"), null);
    }

    /**
     * Create an aliased <code>messaging.user_setting</code> table reference
     */
    public UserSetting(String alias) {
        this(DSL.name(alias), USER_SETTING);
    }

    /**
     * Create an aliased <code>messaging.user_setting</code> table reference
     */
    public UserSetting(Name alias) {
        this(alias, USER_SETTING);
    }

    private UserSetting(Name alias, Table<UserSettingRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserSetting(Name alias, Table<UserSettingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Пользовательские настройки уведомлений"), TableOptions.table());
    }

    public <O extends Record> UserSetting(Table<O> child, ForeignKey<O, UserSettingRecord> key) {
        super(child, key, USER_SETTING);
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.USER_SETTING_USER_ID_MSG_SETTING_ID_UX);
    }

    @Override
    public Identity<UserSettingRecord, Integer> getIdentity() {
        return Keys.IDENTITY_USER_SETTING;
    }

    @Override
    public UniqueKey<UserSettingRecord> getPrimaryKey() {
        return Keys.USER_SETTING_PKEY;
    }

    @Override
    public List<UniqueKey<UserSettingRecord>> getKeys() {
        return Arrays.<UniqueKey<UserSettingRecord>>asList(Keys.USER_SETTING_PKEY);
    }

    @Override
    public List<ForeignKey<UserSettingRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<UserSettingRecord, ?>>asList(Keys.USER_SETTING__USER_SETTING_MSG_SETTINGS_ID_MESSAGE_SETTING_ID_FK, Keys.USER_SETTING__USER_SETTING_CHANNEL_ID_CHANNEL_ID_FK, Keys.USER_SETTING__USER_SETTING_TENANT_CODE_FKEY);
    }

    public MessageSetting messageSetting() {
        return new MessageSetting(this, Keys.USER_SETTING__USER_SETTING_MSG_SETTINGS_ID_MESSAGE_SETTING_ID_FK);
    }

    public Channel channel() {
        return new Channel(this, Keys.USER_SETTING__USER_SETTING_CHANNEL_ID_CHANNEL_ID_FK);
    }

    public Tenant tenant() {
        return new Tenant(this, Keys.USER_SETTING__USER_SETTING_TENANT_CODE_FKEY);
    }

    @Override
    public UserSetting as(String alias) {
        return new UserSetting(DSL.name(alias), this);
    }

    @Override
    public UserSetting as(Name alias) {
        return new UserSetting(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserSetting rename(String name) {
        return new UserSetting(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserSetting rename(Name name) {
        return new UserSetting(name, null);
    }

    // -------------------------------------------------------------------------
    // Row7 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row7<Integer, AlertType, Boolean, String, Integer, String, String> fieldsRow() {
        return (Row7) super.fieldsRow();
    }
}
