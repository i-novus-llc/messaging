/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.api.model.AlertType;
import ru.inovus.messaging.impl.jooq.Indexes;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Public;
import ru.inovus.messaging.impl.jooq.tables.records.UserSettingRecord;
import ru.inovus.messaging.impl.util.AlertTypeConverter;

import java.util.Arrays;
import java.util.List;

import static org.jooq.impl.SQLDataType.*;


/**
 * Пользовательские настройки уведомлений
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class UserSetting extends TableImpl<UserSettingRecord> {

    private static final long serialVersionUID = -930066203;

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
    public final TableField<UserSettingRecord, Integer> ID = createField(DSL.name("id"), INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('user_setting_id_seq'::regclass)", INTEGER)), this, "Уникальный идентификатор");

    /**
     * The column <code>public.user_setting.alert_type</code>. Тип предупреждения
     */
    public final TableField<UserSettingRecord, AlertType> ALERT_TYPE = createField(DSL.name("alert_type"), VARCHAR.nullable(false), this, "Тип предупреждения", new AlertTypeConverter());

    /**
     * The column <code>public.user_setting.is_disabled</code>. Признак выключения уведомления
     */
    public final TableField<UserSettingRecord, Boolean> IS_DISABLED = createField(DSL.name("is_disabled"), BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", BOOLEAN)), this, "Признак выключения уведомления");

    /**
     * The column <code>public.user_setting.user_id</code>. Идентификатор пользователя
     */
    public final TableField<UserSettingRecord, String> USER_ID = createField(DSL.name("user_id"), VARCHAR.nullable(false), this, "Идентификатор пользователя");

    /**
     * The column <code>public.user_setting.msg_setting_id</code>.
     */
    public final TableField<UserSettingRecord, Integer> MSG_SETTING_ID = createField(DSL.name("msg_setting_id"), INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.user_setting.send_channel</code>. Канал отправки
     */
    public final TableField<UserSettingRecord, String> SEND_CHANNEL = createField(DSL.name("send_channel"), VARCHAR, this, "Канал отправки");

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
        super(alias, null, aliased, parameters, DSL.comment("Пользовательские настройки уведомлений"), TableOptions.table());
    }

    public <O extends Record> UserSetting(Table<O> child, ForeignKey<O, UserSettingRecord> key) {
        super(child, key, USER_SETTING);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
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
        return Arrays.<ForeignKey<UserSettingRecord, ?>>asList(Keys.USER_SETTING__USER_SETTING_MSG_SETTINGS_ID_MESSAGE_SETTING_ID_FK);
    }

    public MessageSetting messageSetting() {
        return new MessageSetting(this, Keys.USER_SETTING__USER_SETTING_MSG_SETTINGS_ID_MESSAGE_SETTING_ID_FK);
    }

    @Override
    public List<Check<UserSettingRecord>> getChecks() {
        return Arrays.<Check<UserSettingRecord>>asList(
                Internal.createCheck(this, DSL.name("user_setting_alert_type_check"), "(((alert_type)::text = ANY ((ARRAY['BLOCKER'::character varying, 'POPUP'::character varying, 'HIDDEN'::character varying])::text[])))", true)
        );
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
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<Integer, AlertType, Boolean, String, Integer, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
