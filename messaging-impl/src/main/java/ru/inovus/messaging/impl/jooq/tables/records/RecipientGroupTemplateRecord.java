package ru.inovus.messaging.impl.jooq.tables.records;

import org.jooq.impl.UpdatableRecordImpl;
import ru.inovus.messaging.impl.jooq.Tables;

public class RecipientGroupTemplateRecord extends UpdatableRecordImpl<RecipientGroupTemplateRecord> {

    public RecipientGroupTemplateRecord() {
        super(Tables.RECIPIENT_GROUP_TEMPLATE);
    }

    public void setId(Integer value) {
        set(0, value);
    }

    public Integer getId() {
        return (Integer) get(0);
    }


    public void setRecipientGroupId(Integer value) {
        set(1, value);
    }

    public Integer getRecipientGroupId() {
        return (Integer) get(1);
    }

    public void setMessageTemplateId(Integer value) {
        set(2, value);
    }

    public Integer getMessageTemplateId() {
        return (Integer) get(2);
    }

    public void setMessageTemplateCode(String value) {
        set(3, value);
    }

    public String getMessageTemplateCode() {
        return (String) get(3);
    }

}
