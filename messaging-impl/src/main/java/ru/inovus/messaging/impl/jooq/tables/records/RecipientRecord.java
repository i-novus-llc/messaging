/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
import ru.inovus.messaging.impl.jooq.tables.Recipient;

import javax.annotation.Generated;
import java.time.LocalDateTime;


/**
 * Получатели сообщения
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RecipientRecord extends UpdatableRecordImpl<RecipientRecord> implements Record5<Integer, String, String, LocalDateTime, Integer> {

    private static final long serialVersionUID = -1180076892;

    /**
     * Setter for <code>public.recipient.id</code>. Уникальный идентификатор
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.recipient.id</code>. Уникальный идентификатор
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.recipient.recipient</code>. Получатель
     */
    public void setRecipient(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.recipient.recipient</code>. Получатель
     */
    public String getRecipient() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.recipient.message_id</code>. Ссылка на сообщение
     */
    public void setMessageId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.recipient.message_id</code>. Ссылка на сообщение
     */
    public String getMessageId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.recipient.read_at</code>. Помечено прочтенным (дата и время)
     */
    public void setReadAt(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.recipient.read_at</code>. Помечено прочтенным (дата и время)
     */
    public LocalDateTime getReadAt() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>public.recipient.user_id</code>.
     */
    public void setUserId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.recipient.user_id</code>.
     */
    public Integer getUserId() {
        return (Integer) get(4);
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
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, String, String, LocalDateTime, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, String, String, LocalDateTime, Integer> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Recipient.RECIPIENT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Recipient.RECIPIENT.RECIPIENT_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Recipient.RECIPIENT.MESSAGE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field4() {
        return Recipient.RECIPIENT.READ_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return Recipient.RECIPIENT.USER_ID;
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
        return getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getMessageId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime component4() {
        return getReadAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getUserId();
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
        return getRecipient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getMessageId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value4() {
        return getReadAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientRecord value2(String value) {
        setRecipient(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientRecord value3(String value) {
        setMessageId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientRecord value4(LocalDateTime value) {
        setReadAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientRecord value5(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientRecord values(Integer value1, String value2, String value3, LocalDateTime value4, Integer value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RecipientRecord
     */
    public RecipientRecord() {
        super(Recipient.RECIPIENT);
    }

    /**
     * Create a detached, initialised RecipientRecord
     */
    public RecipientRecord(Integer id, String recipient, String messageId, LocalDateTime readAt, Integer userId) {
        super(Recipient.RECIPIENT);

        set(0, id);
        set(1, recipient);
        set(2, messageId);
        set(3, readAt);
        set(4, userId);
    }
}