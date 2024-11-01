package ru.inovus.messaging.impl.jooq.tables.records;

import org.jooq.impl.UpdatableRecordImpl;
import ru.inovus.messaging.impl.jooq.Tables;

/**
 * Группы получателей
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RecipientGroupRecord extends UpdatableRecordImpl<RecipientGroupRecord>  {

    public RecipientGroupRecord() {
        super(Tables.RECIPIENT_GROUP);
    }

    public void setId(Integer value) {
        set(0, value);
    }

    public Integer getId() {
        return (Integer) get(0);
    }

    public void setName(String value) {
        set(1, value);
    }

    public String getName() {
        return (String) get(1);
    }

    public void setDescription(String value) {
        set(2, value);
    }

    public String getDescription() {
        return (String) get(2);
    }

    public void setTenantCode(String value) {
        set(3, value);
    }

    public String getTenantCode() {
        return (String) get(3);
    }

}
