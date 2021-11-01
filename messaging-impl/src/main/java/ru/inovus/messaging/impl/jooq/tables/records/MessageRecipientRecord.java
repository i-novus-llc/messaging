/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import ru.inovus.messaging.api.model.enums.MessageStatusType;
import ru.inovus.messaging.impl.jooq.tables.MessageRecipient;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Получатель уведомления
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MessageRecipientRecord extends UpdatableRecordImpl<MessageRecipientRecord> implements Record8<Long, UUID, LocalDateTime, String, String, MessageStatusType, LocalDateTime, String> {

    private static final long serialVersionUID = 1128766589;

    /**
     * Setter for <code>messaging.message_recipient.id</code>. Уникальный идентификатор
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.id</code>. Уникальный идентификатор
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>messaging.message_recipient.message_id</code>. Идентификатор уведомления
     */
    public void setMessageId(UUID value) {
        set(1, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.message_id</code>. Идентификатор уведомления
     */
    public UUID getMessageId() {
        return (UUID) get(1);
    }

    /**
     * Setter for <code>messaging.message_recipient.status_time</code>. Время установки статуса
     */
    public void setStatusTime(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.status_time</code>. Время установки статуса
     */
    public LocalDateTime getStatusTime() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>messaging.message_recipient.recipient_name</code>. Имя контакта получателя
     */
    public void setRecipientName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.recipient_name</code>. Имя контакта получателя
     */
    public String getRecipientName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>messaging.message_recipient.recipient_username</code>. Уникальное имя пользователя из провайдера
     */
    public void setRecipientUsername(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.recipient_username</code>. Уникальное имя пользователя из провайдера
     */
    public String getRecipientUsername() {
        return (String) get(4);
    }

    /**
     * Setter for <code>messaging.message_recipient.status</code>. Текущий статус отправки уведомления получателю
     */
    public void setStatus(MessageStatusType value) {
        set(5, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.status</code>. Текущий статус отправки уведомления получателю
     */
    public MessageStatusType getStatus() {
        return (MessageStatusType) get(5);
    }

    /**
     * Setter for <code>messaging.message_recipient.departured_at</code>. Дата и время фактической отправки уведомления
     */
    public void setDeparturedAt(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.departured_at</code>. Дата и время фактической отправки уведомления
     */
    public LocalDateTime getDeparturedAt() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>messaging.message_recipient.send_message_error</code>. Сообщение ошибки отправки уведомления
     */
    public void setSendMessageError(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>messaging.message_recipient.send_message_error</code>. Сообщение ошибки отправки уведомления
     */
    public String getSendMessageError() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, UUID, LocalDateTime, String, String, MessageStatusType, LocalDateTime, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, UUID, LocalDateTime, String, String, MessageStatusType, LocalDateTime, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return MessageRecipient.MESSAGE_RECIPIENT.ID;
    }

    @Override
    public Field<UUID> field2() {
        return MessageRecipient.MESSAGE_RECIPIENT.MESSAGE_ID;
    }

    @Override
    public Field<LocalDateTime> field3() {
        return MessageRecipient.MESSAGE_RECIPIENT.STATUS_TIME;
    }

    @Override
    public Field<String> field4() {
        return MessageRecipient.MESSAGE_RECIPIENT.RECIPIENT_NAME;
    }

    @Override
    public Field<String> field5() {
        return MessageRecipient.MESSAGE_RECIPIENT.RECIPIENT_USERNAME;
    }

    @Override
    public Field<MessageStatusType> field6() {
        return MessageRecipient.MESSAGE_RECIPIENT.STATUS;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return MessageRecipient.MESSAGE_RECIPIENT.DEPARTURED_AT;
    }

    @Override
    public Field<String> field8() {
        return MessageRecipient.MESSAGE_RECIPIENT.SEND_MESSAGE_ERROR;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public UUID component2() {
        return getMessageId();
    }

    @Override
    public LocalDateTime component3() {
        return getStatusTime();
    }

    @Override
    public String component4() {
        return getRecipientName();
    }

    @Override
    public String component5() {
        return getRecipientUsername();
    }

    @Override
    public MessageStatusType component6() {
        return getStatus();
    }

    @Override
    public LocalDateTime component7() {
        return getDeparturedAt();
    }

    @Override
    public String component8() {
        return getSendMessageError();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public UUID value2() {
        return getMessageId();
    }

    @Override
    public LocalDateTime value3() {
        return getStatusTime();
    }

    @Override
    public String value4() {
        return getRecipientName();
    }

    @Override
    public String value5() {
        return getRecipientUsername();
    }

    @Override
    public MessageStatusType value6() {
        return getStatus();
    }

    @Override
    public LocalDateTime value7() {
        return getDeparturedAt();
    }

    @Override
    public String value8() {
        return getSendMessageError();
    }

    @Override
    public MessageRecipientRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value2(UUID value) {
        setMessageId(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value3(LocalDateTime value) {
        setStatusTime(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value4(String value) {
        setRecipientName(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value5(String value) {
        setRecipientUsername(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value6(MessageStatusType value) {
        setStatus(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value7(LocalDateTime value) {
        setDeparturedAt(value);
        return this;
    }

    @Override
    public MessageRecipientRecord value8(String value) {
        setSendMessageError(value);
        return this;
    }

    @Override
    public MessageRecipientRecord values(Long value1, UUID value2, LocalDateTime value3, String value4, String value5, MessageStatusType value6, LocalDateTime value7, String value8) {
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
     * Create a detached MessageRecipientRecord
     */
    public MessageRecipientRecord() {
        super(MessageRecipient.MESSAGE_RECIPIENT);
    }

    /**
     * Create a detached, initialised MessageRecipientRecord
     */
    public MessageRecipientRecord(Long id, UUID messageId, LocalDateTime statusTime, String recipientName, String recipientUsername, MessageStatusType status, LocalDateTime departuredAt, String sendMessageError) {
        super(MessageRecipient.MESSAGE_RECIPIENT);

        set(0, id);
        set(1, messageId);
        set(2, statusTime);
        set(3, recipientName);
        set(4, recipientUsername);
        set(5, status);
        set(6, departuredAt);
        set(7, sendMessageError);
    }
}
