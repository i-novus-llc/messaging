package ru.inovus.messaging.impl.jooq.tables;

import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientGroupUserRecord;

import java.util.List;

public class RecipientGroupUser extends TableImpl<RecipientGroupUserRecord> {

    public static final RecipientGroupUser RECIPIENT_GROUP_USER = new RecipientGroupUser();

    public final TableField<RecipientGroupUserRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");
    public final TableField<RecipientGroupUserRecord, Integer> RECIPIENT_GROUP_ID = createField(DSL.name("recipient_group_id"), SQLDataType.INTEGER.nullable(false), this, "Ссылка на группу");
    public final TableField<RecipientGroupUserRecord, String> RECIPIENT_NAME = createField(DSL.name("recipient_name"), SQLDataType.VARCHAR.nullable(false), this, "Имя получателя");
    public final TableField<RecipientGroupUserRecord, String> RECIPIENT_USERNAME = createField(DSL.name("recipient_username"), SQLDataType.VARCHAR.nullable(false), this, "ID получателя");


    public RecipientGroupUser() {
        super(DSL.name("recipient_group_user"));
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public UniqueKey<RecipientGroupUserRecord> getPrimaryKey() {
        return Keys.RECIPIENT_GROUP_USER_PKEY;
    }

    @Override
    public List<ForeignKey<RecipientGroupUserRecord, ?>> getReferences() {
        return List.of(Keys.RECIPIENT_GROUP_USER_RECIPIENT_GROUP_ID_FKEY);
    }

    @Override
    public Class<RecipientGroupUserRecord> getRecordType() {
        return RecipientGroupUserRecord.class;
    }
}
