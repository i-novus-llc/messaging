/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Public;
import ru.inovus.messaging.impl.jooq.tables.records.ChannelRecord;


/**
 * Канал отправки
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Channel extends TableImpl<ChannelRecord> {

    private static final long serialVersionUID = -1440393253;

    /**
     * The reference instance of <code>public.channel</code>
     */
    public static final Channel CHANNEL = new Channel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ChannelRecord> getRecordType() {
        return ChannelRecord.class;
    }

    /**
     * The column <code>public.channel.id</code>. Уникальный код канала
     */
    public final TableField<ChannelRecord, String> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Уникальный код канала");

    /**
     * The column <code>public.channel.name</code>. Имя канала для отображения на UI
     */
    public final TableField<ChannelRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Имя канала для отображения на UI");

    /**
     * The column <code>public.channel.queue_name</code>. Имя очереди канала
     */
    public final TableField<ChannelRecord, String> QUEUE_NAME = createField(DSL.name("queue_name"), org.jooq.impl.SQLDataType.VARCHAR.nullable(false), this, "Имя очереди канала");

    /**
     * Create a <code>public.channel</code> table reference
     */
    public Channel() {
        this(DSL.name("channel"), null);
    }

    /**
     * Create an aliased <code>public.channel</code> table reference
     */
    public Channel(String alias) {
        this(DSL.name(alias), CHANNEL);
    }

    /**
     * Create an aliased <code>public.channel</code> table reference
     */
    public Channel(Name alias) {
        this(alias, CHANNEL);
    }

    private Channel(Name alias, Table<ChannelRecord> aliased) {
        this(alias, aliased, null);
    }

    private Channel(Name alias, Table<ChannelRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Канал отправки"), TableOptions.table());
    }

    public <O extends Record> Channel(Table<O> child, ForeignKey<O, ChannelRecord> key) {
        super(child, key, CHANNEL);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<ChannelRecord> getPrimaryKey() {
        return Keys.CHANNEL_PKEY;
    }

    @Override
    public List<UniqueKey<ChannelRecord>> getKeys() {
        return Arrays.<UniqueKey<ChannelRecord>>asList(Keys.CHANNEL_PKEY);
    }

    @Override
    public Channel as(String alias) {
        return new Channel(DSL.name(alias), this);
    }

    @Override
    public Channel as(Name alias) {
        return new Channel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Channel rename(String name) {
        return new Channel(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Channel rename(Name name) {
        return new Channel(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<String, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
