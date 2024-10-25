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
import ru.inovus.messaging.impl.jooq.tables.records.RecipientGroupTemplateRecord;

import java.util.List;

public class RecipientGroupTemplate extends TableImpl<RecipientGroupTemplateRecord> {

    public static final RecipientGroupTemplate RECIPIENT_GROUP_TEMPLATE = new RecipientGroupTemplate();

    public final TableField<RecipientGroupTemplateRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false), this, "Уникальный идентификатор");
    public final TableField<RecipientGroupTemplateRecord, Integer> RECIPIENT_GROUP_ID = createField(DSL.name("recipient_group_id"), SQLDataType.INTEGER.nullable(false), this, "Ссылка на группу");
    public final TableField<RecipientGroupTemplateRecord, Integer> MESSAGE_TEMPLATE_ID = createField(DSL.name("message_template_id"), SQLDataType.INTEGER.nullable(false), this, "Ссылка на шаблон'");
    public final TableField<RecipientGroupTemplateRecord, String> MESSAGE_TEMPLATE_CODE = createField(DSL.name("message_template_code"), SQLDataType.VARCHAR.nullable(false), this, "Код шаблона'");


    public RecipientGroupTemplate() {
        super(DSL.name("recipient_group_template"));
    }

    @Override
    public Schema getSchema() {
        return Messaging.MESSAGING;
    }

    @Override
    public UniqueKey<RecipientGroupTemplateRecord> getPrimaryKey() {
        return Keys.RECIPIENT_GROUP_TEMPLATE_PKEY;
    }

    @Override
    public List<ForeignKey<RecipientGroupTemplateRecord, ?>> getReferences() {
        return List.of(Keys.RECIPIENT_GROUP_TEMPLATE_RECIPIENT_GROUP_ID_FKEY, Keys.RECIPIENT_GROUP_TEMPLATE_MESSAGE_TEMPLATE_CODE_FKEY);
    }

    @Override
    public Class<RecipientGroupTemplateRecord> getRecordType() {
        return RecipientGroupTemplateRecord.class;
    }
}
