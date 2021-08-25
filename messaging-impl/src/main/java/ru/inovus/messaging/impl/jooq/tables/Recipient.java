/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;

import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TimestampToLocalDateTimeConverter;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Public;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Получатели сообщения
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Recipient extends TableImpl<RecipientRecord> {

    private static final long serialVersionUID = 1226022086;

    /**
     * The reference instance of <code>public.recipient</code>
     */
    public static final Recipient RECIPIENT = new Recipient();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RecipientRecord> getRecordType() {
        return RecipientRecord.class;
    }

    /**
     * The column <code>public.recipient.id</code>. Уникальный идентификатор
     */
    public final TableField<RecipientRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>public.recipient.recipient</code>. Получатель
     */
    public final TableField<RecipientRecord, String> RECIPIENT_ = createField(DSL.name("recipient"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Получатель");

    /**
     * The column <code>public.recipient.message_id</code>. Ссылка на сообщение
     */
    public final TableField<RecipientRecord, UUID> MESSAGE_ID = createField(DSL.name("message_id"), org.jooq.impl.SQLDataType.UUID, this, "Ссылка на сообщение");

    /**
     * The column <code>public.recipient.read_at</code>. Помечено прочтенным (дата и время)
     */
    public final TableField<RecipientRecord, LocalDateTime> READ_AT = createField(DSL.name("read_at"), SQLDataType.TIMESTAMP, this, "Помечено прочтенным (дата и время)", new TimestampToLocalDateTimeConverter());

    /**
     * The column <code>public.recipient.email</code>.
     */
    public final TableField<RecipientRecord, String> EMAIL = createField(DSL.name("email"), org.jooq.impl.SQLDataType.VARCHAR, this, "");

    /**
     * Create a <code>public.recipient</code> table reference
     */
    public Recipient() {
        this(DSL.name("recipient"), null);
    }

    /**
     * Create an aliased <code>public.recipient</code> table reference
     */
    public Recipient(String alias) {
        this(DSL.name(alias), RECIPIENT);
    }

    /**
     * Create an aliased <code>public.recipient</code> table reference
     */
    public Recipient(Name alias) {
        this(alias, RECIPIENT);
    }

    private Recipient(Name alias, Table<RecipientRecord> aliased) {
        this(alias, aliased, null);
    }

    private Recipient(Name alias, Table<RecipientRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Получатели сообщения"), TableOptions.table());
    }

    public <O extends Record> Recipient(Table<O> child, ForeignKey<O, RecipientRecord> key) {
        super(child, key, RECIPIENT);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<RecipientRecord> getPrimaryKey() {
        return Keys.RECIPIENT_PKEY;
    }

    @Override
    public List<UniqueKey<RecipientRecord>> getKeys() {
        return Arrays.<UniqueKey<RecipientRecord>>asList(Keys.RECIPIENT_PKEY);
    }

    @Override
    public List<ForeignKey<RecipientRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<RecipientRecord, ?>>asList(Keys.RECIPIENT__RECIPIENT_MESSAGE_ID_FKEY);
    }

    public Message message() {
        return new Message(this, Keys.RECIPIENT__RECIPIENT_MESSAGE_ID_FKEY);
    }

    @Override
    public Recipient as(String alias) {
        return new Recipient(DSL.name(alias), this);
    }

    @Override
    public Recipient as(Name alias) {
        return new Recipient(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Recipient rename(String name) {
        return new Recipient(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Recipient rename(Name name) {
        return new Recipient(name, null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, String, UUID, LocalDateTime, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }
}
