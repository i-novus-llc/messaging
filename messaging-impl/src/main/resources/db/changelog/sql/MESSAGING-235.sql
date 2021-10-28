-- drop user_setting
DROP TABLE messaging.user_setting;

-- rename message_setting
ALTER TABLE messaging.message_setting RENAME TO message_template;
COMMENT ON TABLE messaging.message_template IS 'Шаблоны уведомлений';

-- rename and drop sequences
DROP SEQUENCE IF EXISTS messaging.user_setting_id_seq;
ALTER SEQUENCE messaging.message_setting_id_seq RENAME TO message_template_id_seq;
ALTER SEQUENCE messaging.recipient_id_seq RENAME TO message_recipient_id_seq;

-- rename constraints
ALTER TABLE messaging.message_template RENAME CONSTRAINT
        message_setting_pkey TO message_template_pkey;
ALTER TABLE messaging.message_template RENAME CONSTRAINT
        message_setting_tenant_code_fkey TO message_template_tenant_code_fkey;
ALTER TABLE messaging.message_template RENAME CONSTRAINT
        message_setting_channel_id_channel_id_fk TO message_template_channel_id_channel_id_fk;
ALTER TABLE messaging.message_recipient RENAME CONSTRAINT
        recipient_pkey TO message_recipient_pkey;
ALTER TABLE messaging.message_recipient RENAME CONSTRAINT
        recipient_message_id_fkey TO message_recipient_message_id_fkey;

-- create and drop indexes
DROP INDEX messaging.ix_message_system_id;
CREATE INDEX ON messaging.message (tenant_code);
CREATE INDEX ON messaging.message_template (tenant_code);

-- add new constraints
ALTER TABLE messaging.message_template ADD UNIQUE (code);

-- rename notification_type column
ALTER TABLE messaging.message
    RENAME COLUMN notification_type TO template_code;


