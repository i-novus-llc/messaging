package ru.inovus.messaging.impl.jooq.tables.records;

import org.jooq.impl.UpdatableRecordImpl;
import ru.inovus.messaging.impl.jooq.Tables;
import ru.inovus.messaging.impl.jooq.tables.RecipientGroup;

/**
 * Группы получателей
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RecipientGroupRecord extends UpdatableRecordImpl<RecipientGroupRecord>  {

    public RecipientGroupRecord() {
        super(Tables.RECIPIENT_GROUP);
    }

    public void setId(Integer value) {
        set(RecipientGroup.RECIPIENT_GROUP.ID, value);
    }

    public Integer getId() {
        return (Integer) get(RecipientGroup.RECIPIENT_GROUP.ID);
    }

    public void setName(String value) {
        set(RecipientGroup.RECIPIENT_GROUP.NAME, value);
    }

    public String getName() {
        return (String) get(RecipientGroup.RECIPIENT_GROUP.NAME);
    }

    public void setDescription(String value) {
        set(RecipientGroup.RECIPIENT_GROUP.DESCRIPTION, value);
    }

    public String getDescription() {
        return (String) get(RecipientGroup.RECIPIENT_GROUP.DESCRIPTION);
    }

    public void setTenantCode(String value) {
        set(RecipientGroup.RECIPIENT_GROUP.TENANT_CODE, value);
    }

    public String getTenantCode() {
        return (String) get(RecipientGroup.RECIPIENT_GROUP.TENANT_CODE);
    }

    public void setCode(String value) { set(RecipientGroup.RECIPIENT_GROUP.CODE, value);}

    public String getCode() {return (String) get(RecipientGroup.RECIPIENT_GROUP.CODE);}

}
