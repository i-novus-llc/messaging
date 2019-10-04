/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


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

import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.impl.AlertTypeConverter;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Public;
import ru.inovus.messaging.impl.jooq.tables.records.UserSettingRecord;


/**
 * Пользовательские настройки уведомлений
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserSetting extends TableImpl<UserSettingRecord> {

    private static final long serialVersionUID = 656920315;

    /**
     * The reference instance of <code>public.user_setting</code>
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
     * The column <code>public.user_setting.id</code>. Уникальный идентификатор
     */
    public final TableField<UserSettingRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>public.user_setting.alert_type</code>. Тип предупреждения
     */
    public final TableField<UserSettingRecord, AlertType> ALERT_TYPE = createField("alert_type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Тип предупреждения", new AlertTypeConverter());

    /**
     * The column <code>public.user_setting.is_disabled</code>. Признак выключения уведомления
     */
    public final TableField<UserSettingRecord, Boolean> IS_DISABLED = createField("is_disabled", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "Признак выключения уведомления");

    /**
     * The column <code>public.user_setting.user_id</code>. Идентификатор пользователя
     */
    public final TableField<UserSettingRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Идентификатор пользователя");

    /**
     * The column <code>public.user_setting.send_notice</code>.
     */
    public final TableField<UserSettingRecord, Boolean> SEND_NOTICE = createField("send_notice", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.user_setting.send_email</code>.
     */
    public final TableField<UserSettingRecord, Boolean> SEND_EMAIL = createField("send_email", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.user_setting.id</code>. Уникальный идентификатор
     */
    public final TableField<UserSettingRecord, Integer> MSG_SETTING_ID = createField("msg_settings_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Идентификатор шаблона уведомлений, который меняет пользователь");

    /**
     * Create a <code>public.user_setting</code> table reference
     */
    public UserSetting() {
        this(DSL.name("user_setting"), null);
    }

    /**
     * Create an aliased <code>public.user_setting</code> table reference
     */
    public UserSetting(String alias) {
        this(DSL.name(alias), USER_SETTING);
    }

    /**
     * Create an aliased <code>public.user_setting</code> table reference
     */
    public UserSetting(Name alias) {
        this(alias, USER_SETTING);
    }

    private UserSetting(Name alias, Table<UserSettingRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserSetting(Name alias, Table<UserSettingRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Пользовательские настройки уведомлений"));
    }

    public <O extends Record> UserSetting(Table<O> child, ForeignKey<O, UserSettingRecord> key) {
        super(child, key, USER_SETTING);
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
        return Arrays.<Index>asList(Indexes.USER_SETTING_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UserSettingRecord> getPrimaryKey() {
        return Keys.USER_SETTING_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UserSettingRecord>> getKeys() {
        return Arrays.<UniqueKey<UserSettingRecord>>asList(Keys.USER_SETTING_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<UserSettingRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<UserSettingRecord, ?>>asList(Keys.USER_SETTING__FK_USER_SETTING_ID);
    }

    public MessageSetting messageSetting() {
        return new MessageSetting(this, Keys.USER_SETTING__FK_USER_SETTING_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserSetting as(String alias) {
        return new UserSetting(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
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
}
