package ru.inovus.messaging.impl.jooq.tables;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;
import ru.inovus.messaging.impl.jooq.Keys;
import ru.inovus.messaging.impl.jooq.Messaging;
import ru.inovus.messaging.impl.jooq.tables.records.RecipientGroupRecord;

import java.util.List;

public class RecipientGroup extends TableImpl<RecipientGroupRecord> {

    public static final RecipientGroup RECIPIENT_GROUP = new RecipientGroup();

    public final TableField<RecipientGroupRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");
    public final TableField<RecipientGroupRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR.nullable(false), this, "Наименование группы");
    public final TableField<RecipientGroupRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.VARCHAR, this, "Описание группы");
    public final TableField<RecipientGroupRecord, String> TENANT_CODE = createField(DSL.name("tenant_code"), SQLDataType.VARCHAR, this, "Тенант, к которому относится настройка");


    public RecipientGroup() {
        super(DSL.name("recipient_group"));
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public UniqueKey<RecipientGroupRecord> getPrimaryKey() {
        return Keys.RECIPIENT_GROUP_PKEY;
    }

    @Override
    public List<ForeignKey<RecipientGroupRecord, ?>> getReferences() {
        return List.of(Keys.RECIPIENT_GROUP_TENANT_CODE_FKEY);
    }

    @Override
    public Class<RecipientGroupRecord> getRecordType() {
        return RecipientGroupRecord.class;
    }
}
