package ru.inovus.messaging.impl.jooq.tables.records;

import org.jooq.impl.UpdatableRecordImpl;
import ru.inovus.messaging.impl.jooq.Tables;

public class RecipientGroupUserRecord extends UpdatableRecordImpl<RecipientGroupUserRecord> {

    public RecipientGroupUserRecord() {
        super(Tables.RECIPIENT_GROUP_USER);
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

    public void setRecipientName(String value) {
        set(2, value);
    }

    public String getRecipientName() {
        return (String) get(2);
    }

    public void setRecipientUsername(String value) {
        set(3, value);
    }

    public String getRecipientUsername() {
        return (String) get(3);
    }
}
