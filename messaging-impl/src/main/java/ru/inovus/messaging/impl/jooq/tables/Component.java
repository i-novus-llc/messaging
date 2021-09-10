/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables;


import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.ComponentRecord;

import java.util.Arrays;
import java.util.List;


/**
 * Компоненты системы
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Component extends TableImpl<ComponentRecord> {

    private static final long serialVersionUID = 1523508789;

    /**
     * The reference instance of <code>messaging.component</code>
     */
    public static final Component COMPONENT = new Component();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ComponentRecord> getRecordType() {
        return ComponentRecord.class;
    }

    /**
     * The column <code>messaging.component.id</code>. Уникальный идентификатор
     */
    public final TableField<ComponentRecord, Integer> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");

    /**
     * The column <code>messaging.component.name</code>. Наименование компонента
     */
    public final TableField<ComponentRecord, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR, this, "Наименование компонента");

    /**
     * Create a <code>messaging.component</code> table reference
     */
    public Component() {
        this(DSL.name("component"), null);
    }

    /**
     * Create an aliased <code>messaging.component</code> table reference
     */
    public Component(String alias) {
        this(DSL.name(alias), COMPONENT);
    }

    /**
     * Create an aliased <code>messaging.component</code> table reference
     */
    public Component(Name alias) {
        this(alias, COMPONENT);
    }

    private Component(Name alias, Table<ComponentRecord> aliased) {
        this(alias, aliased, null);
    }

    private Component(Name alias, Table<ComponentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Компоненты системы"), TableOptions.table());
    }

    public <O extends Record> Component(Table<O> child, ForeignKey<O, ComponentRecord> key) {
        super(child, key, COMPONENT);
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public UniqueKey<ComponentRecord> getPrimaryKey() {
        return Keys.COMPONENT_PKEY;
    }

    @Override
    public List<UniqueKey<ComponentRecord>> getKeys() {
        return Arrays.<UniqueKey<ComponentRecord>>asList(Keys.COMPONENT_PKEY);
    }

    @Override
    public Component as(String alias) {
        return new Component(DSL.name(alias), this);
    }

    @Override
    public Component as(Name alias) {
        return new Component(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Component rename(String name) {
        return new Component(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Component rename(Name name) {
        return new Component(name, null);
    }

    // -------------------------------------------------------------------------
    // Row2 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }
}
