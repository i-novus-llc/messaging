/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables.records;


import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;

import ru.inovus.messaging.api.AlertType;
import ru.inovus.messaging.api.Severity;
import ru.inovus.messaging.impl.jooq.tables.Message;


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
public class MessageRecord extends UpdatableRecordImpl<MessageRecord> implements Record8<Integer, String, String, Severity, AlertType, LocalDateTime, LocalDateTime, String> {

    private static final long serialVersionUID = -895949718;

    /**
     * Setter for <code>public.message.id</code>. Уникальный идентификатор
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.message.id</code>. Уникальный идентификатор
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.message.caption</code>. Заголовок
     */
    public void setCaption(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.message.caption</code>. Заголовок
     */
    public String getCaption() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.message.text</code>. Содержимое сообщения
     */
    public void setText(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.message.text</code>. Содержимое сообщения
     */
    public String getText() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.message.severity</code>. Жесткость сообщения
     */
    public void setSeverity(Severity value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.message.severity</code>. Жесткость сообщения
     */
    public Severity getSeverity() {
        return (Severity) get(3);
    }

    /**
     * Setter for <code>public.message.alert_type</code>. Тип предупреждения
     */
    public void setAlertType(AlertType value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.message.alert_type</code>. Тип предупреждения
     */
    public AlertType getAlertType() {
        return (AlertType) get(4);
    }

    /**
     * Setter for <code>public.message.sent_at</code>. Отправлено (дата и время)
     */
    public void setSentAt(LocalDateTime value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.message.sent_at</code>. Отправлено (дата и время)
     */
    public LocalDateTime getSentAt() {
        return (LocalDateTime) get(5);
    }

    /**
     * Setter for <code>public.message.read_at</code>. Помечено прочтенным (дата и время)
     */
    public void setReadAt(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.message.read_at</code>. Помечено прочтенным (дата и время)
     */
    public LocalDateTime getReadAt() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>public.message.recipient</code>. Получатель
     */
    public void setRecipient(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.message.recipient</code>. Получатель
     */
    public String getRecipient() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, String, Severity, AlertType, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, String, Severity, AlertType, LocalDateTime, LocalDateTime, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Message.MESSAGE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Message.MESSAGE.CAPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Message.MESSAGE.TEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Severity> field4() {
        return Message.MESSAGE.SEVERITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<AlertType> field5() {
        return Message.MESSAGE.ALERT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field6() {
        return Message.MESSAGE.SENT_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field7() {
        return Message.MESSAGE.READ_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Message.MESSAGE.RECIPIENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getCaption();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Severity component4() {
        return getSeverity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlertType component5() {
        return getAlertType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component6() {
        return getSentAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component7() {
        return getReadAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getCaption();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Severity value4() {
        return getSeverity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlertType value5() {
        return getAlertType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value6() {
        return getSentAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value7() {
        return getReadAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value2(String value) {
        setCaption(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value3(String value) {
        setText(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value4(Severity value) {
        setSeverity(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value5(AlertType value) {
        setAlertType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value6(LocalDateTime value) {
        setSentAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value7(LocalDateTime value) {
        setReadAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord value8(String value) {
        setRecipient(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageRecord values(Integer value1, String value2, String value3, Severity value4, AlertType value5, LocalDateTime value6, LocalDateTime value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MessageRecord
     */
    public MessageRecord() {
        super(Message.MESSAGE);
    }

    /**
     * Create a detached, initialised MessageRecord
     */
    public MessageRecord(Integer id, String caption, String text, Severity severity, AlertType alertType, LocalDateTime sentAt, LocalDateTime readAt, String recipient) {
        super(Message.MESSAGE);

        set(0, id);
        set(1, caption);
        set(2, text);
        set(3, severity);
        set(4, alertType);
        set(5, sentAt);
        set(6, readAt);
        set(7, recipient);
    }
}
