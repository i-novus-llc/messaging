/*
 * This file is generated by jOOQ.
 */
package ru.inovus.messaging.impl.jooq.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;

import ru.inovus.messaging.impl.jooq.tables.Tenant;


/**
 * Тенанты
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TenantRecord extends UpdatableRecordImpl<TenantRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 1778893056;

    /**
     * Setter for <code>messaging.tenant.code</code>. Уникальный код тенанта
     */
    public void setCode(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>messaging.tenant.code</code>. Уникальный код тенанта
     */
    public String getCode() {
        return (String) get(0);
    }

    /**
     * Setter for <code>messaging.tenant.name</code>. Наименование тенанта
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>messaging.tenant.name</code>. Наименование тенанта
     */
    public String getName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return Tenant.TENANT.CODE;
    }

    @Override
    public Field<String> field2() {
        return Tenant.TENANT.NAME;
    }

    @Override
    public String component1() {
        return getCode();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public String value1() {
        return getCode();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public TenantRecord value1(String value) {
        setCode(value);
        return this;
    }

    @Override
    public TenantRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public TenantRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TenantRecord
     */
    public TenantRecord() {
        super(Tenant.TENANT);
    }

    /**
     * Create a detached, initialised TenantRecord
     */
    public TenantRecord(String code, String name) {
        super(Tenant.TENANT);

        set(0, code);
        set(1, name);
    }
}
