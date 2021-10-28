DROP TABLE messaging.user_setting;
DROP SEQUENCE IF EXISTS messaging.user_setting_id_seq;

ALTER TABLE messaging.message_setting RENAME TO message_template;
COMMENT ON TABLE messaging.message_template IS 'Шаблоны уведомлений';
ALTER SEQUENCE messaging.message_setting_id_seq RENAME TO message_template_id_seq;
ALTER SEQUENCE messaging.recipient_id_seq RENAME TO message_recipient_id_seq;

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

