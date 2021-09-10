/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
import org.jooq.impl.UpdatableRecordImpl;

import ru.inovus.messaging.api.model.enums.AlertType;
import ru.inovus.messaging.api.model.enums.FormationType;
import ru.inovus.messaging.api.model.enums.Severity;
import ru.inovus.messaging.impl.jooq.tables.MessageSetting;


/**
 * Шаблоны уведомлений (общесистемные настройки)
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessageSettingRecord extends UpdatableRecordImpl<MessageSettingRecord> implements Record11<Integer, String, String, Severity, AlertType, Integer, String, Boolean, FormationType, String, String> {

    private static final long serialVersionUID = 53403457;

    /**
     * Setter for <code>messaging.message_setting.id</code>. Уникальный идентификатор
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>messaging.message_setting.id</code>. Уникальный идентификатор
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>messaging.message_setting.caption</code>. Заголовок уведомления
     */
    public void setCaption(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>messaging.message_setting.caption</code>. Заголовок уведомления
     */
    public String getCaption() {
        return (String) get(1);
    }

    /**
     * Setter for <code>messaging.message_setting.text</code>. Содержимое уведомления
     */
    public void setText(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>messaging.message_setting.text</code>. Содержимое уведомления
     */
    public String getText() {
        return (String) get(2);
    }

    /**
     * Setter for <code>messaging.message_setting.severity</code>. Важность уведомления
     */
    public void setSeverity(Severity value) {
        set(3, value);
    }

    /**
     * Getter for <code>messaging.message_setting.severity</code>. Важность уведомления
     */
    public Severity getSeverity() {
        return (Severity) get(3);
    }

    /**
     * Setter for <code>messaging.message_setting.alert_type</code>. Способ отображения уведомления
     */
    public void setAlertType(AlertType value) {
        set(4, value);
    }

    /**
     * Getter for <code>messaging.message_setting.alert_type</code>. Способ отображения уведомления
     */
    public AlertType getAlertType() {
        return (AlertType) get(4);
    }

    /**
     * Setter for <code>messaging.message_setting.component_id</code>. Идентификатор компонента (модуля, подсистемы), к которому относится уведомление
     */
    public void setComponentId(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>messaging.message_setting.component_id</code>. Идентификатор компонента (модуля, подсистемы), к которому относится уведомление
     */
    public Integer getComponentId() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>messaging.message_setting.name</code>. Наименование шаблона уведомления
     */
    public void setName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>messaging.message_setting.name</code>. Наименование шаблона уведомления
     */
    public String getName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>messaging.message_setting.is_disabled</code>. Признак выключения уведомления
     */
    public void setIsDisabled(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>messaging.message_setting.is_disabled</code>. Признак выключения уведомления
     */
    public Boolean getIsDisabled() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>messaging.message_setting.formation_type</code>. Тип формирования уведомления
     */
    public void setFormationType(FormationType value) {
        set(8, value);
    }

    /**
     * Getter for <code>messaging.message_setting.formation_type</code>. Тип формирования уведомления
     */
    public FormationType getFormationType() {
        return (FormationType) get(8);
    }

    /**
     * Setter for <code>messaging.message_setting.code</code>. Код шаблона уведомления
     */
    public void setCode(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>messaging.message_setting.code</code>. Код шаблона уведомления
     */
    public String getCode() {
        return (String) get(9);
    }

    /**
     * Setter for <code>messaging.message_setting.channel_id</code>. Идентификатор канала отправки уведомления
     */
    public void setChannelId(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>messaging.message_setting.channel_id</code>. Идентификатор канала отправки уведомления
     */
    public String getChannelId() {
        return (String) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record11 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, String, String, Severity, AlertType, Integer, String, Boolean, FormationType, String, String> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    @Override
    public Row11<Integer, String, String, Severity, AlertType, Integer, String, Boolean, FormationType, String, String> valuesRow() {
        return (Row11) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return MessageSetting.MESSAGE_SETTING.ID;
    }

    @Override
    public Field<String> field2() {
        return MessageSetting.MESSAGE_SETTING.CAPTION;
    }

    @Override
    public Field<String> field3() {
        return MessageSetting.MESSAGE_SETTING.TEXT;
    }

    @Override
    public Field<Severity> field4() {
        return MessageSetting.MESSAGE_SETTING.SEVERITY;
    }

    @Override
    public Field<AlertType> field5() {
        return MessageSetting.MESSAGE_SETTING.ALERT_TYPE;
    }

    @Override
    public Field<Integer> field6() {
        return MessageSetting.MESSAGE_SETTING.COMPONENT_ID;
    }

    @Override
    public Field<String> field7() {
        return MessageSetting.MESSAGE_SETTING.NAME;
    }

    @Override
    public Field<Boolean> field8() {
        return MessageSetting.MESSAGE_SETTING.IS_DISABLED;
    }

    @Override
    public Field<FormationType> field9() {
        return MessageSetting.MESSAGE_SETTING.FORMATION_TYPE;
    }

    @Override
    public Field<String> field10() {
        return MessageSetting.MESSAGE_SETTING.CODE;
    }

    @Override
    public Field<String> field11() {
        return MessageSetting.MESSAGE_SETTING.CHANNEL_ID;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getCaption();
    }

    @Override
    public String component3() {
        return getText();
    }

    @Override
    public Severity component4() {
        return getSeverity();
    }

    @Override
    public AlertType component5() {
        return getAlertType();
    }

    @Override
    public Integer component6() {
        return getComponentId();
    }

    @Override
    public String component7() {
        return getName();
    }

    @Override
    public Boolean component8() {
        return getIsDisabled();
    }

    @Override
    public FormationType component9() {
        return getFormationType();
    }

    @Override
    public String component10() {
        return getCode();
    }

    @Override
    public String component11() {
        return getChannelId();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getCaption();
    }

    @Override
    public String value3() {
        return getText();
    }

    @Override
    public Severity value4() {
        return getSeverity();
    }

    @Override
    public AlertType value5() {
        return getAlertType();
    }

    @Override
    public Integer value6() {
        return getComponentId();
    }

    @Override
    public String value7() {
        return getName();
    }

    @Override
    public Boolean value8() {
        return getIsDisabled();
    }

    @Override
    public FormationType value9() {
        return getFormationType();
    }

    @Override
    public String value10() {
        return getCode();
    }

    @Override
    public String value11() {
        return getChannelId();
    }

    @Override
    public MessageSettingRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public MessageSettingRecord value2(String value) {
        setCaption(value);
        return this;
    }

    @Override
    public MessageSettingRecord value3(String value) {
        setText(value);
        return this;
    }

    @Override
    public MessageSettingRecord value4(Severity value) {
        setSeverity(value);
        return this;
    }

    @Override
    public MessageSettingRecord value5(AlertType value) {
        setAlertType(value);
        return this;
    }

    @Override
    public MessageSettingRecord value6(Integer value) {
        setComponentId(value);
        return this;
    }

    @Override
    public MessageSettingRecord value7(String value) {
        setName(value);
        return this;
    }

    @Override
    public MessageSettingRecord value8(Boolean value) {
        setIsDisabled(value);
        return this;
    }

    @Override
    public MessageSettingRecord value9(FormationType value) {
        setFormationType(value);
        return this;
    }

    @Override
    public MessageSettingRecord value10(String value) {
        setCode(value);
        return this;
    }

    @Override
    public MessageSettingRecord value11(String value) {
        setChannelId(value);
        return this;
    }

    @Override
    public MessageSettingRecord values(Integer value1, String value2, String value3, Severity value4, AlertType value5, Integer value6, String value7, Boolean value8, FormationType value9, String value10, String value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MessageSettingRecord
     */
    public MessageSettingRecord() {
        super(MessageSetting.MESSAGE_SETTING);
    }

    /**
     * Create a detached, initialised MessageSettingRecord
     */
    public MessageSettingRecord(Integer id, String caption, String text, Severity severity, AlertType alertType, Integer componentId, String name, Boolean isDisabled, FormationType formationType, String code, String channelId) {
        super(MessageSetting.MESSAGE_SETTING);

        set(0, id);
        set(1, caption);
        set(2, text);
        set(3, severity);
        set(4, alertType);
        set(5, componentId);
        set(6, name);
        set(7, isDisabled);
        set(8, formationType);
        set(9, code);
        set(10, channelId);
    }
}
