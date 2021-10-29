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

-- add unique constraint
ALTER TABLE messaging.message_template ADD UNIQUE (code);

-- rename columns
ALTER TABLE messaging.message
    RENAME COLUMN notification_type TO template_code;
ALTER TABLE messaging.message_template
    RENAME COLUMN is_disabled TO enabled;
COMMENT ON COLUMN messaging.message_template.enabled IS 'Признак включения уведомления';
ALTER TABLE messaging.message_template ALTER COLUMN enabled SET DEFAULT true;
UPDATE messaging.message_template SET enabled = NOT enabled;

-- change severity store mechanism
UPDATE messaging.message SET severity =
    (CASE WHEN severity = '10' THEN 'INFO'
          WHEN severity = '20' THEN 'WARNING'
          WHEN severity = '30' THEN 'ERROR'
          WHEN severity = '40' THEN 'SEVERE'
     END);
UPDATE messaging.message_template SET severity =
    (CASE WHEN severity = '10' THEN 'INFO'
          WHEN severity = '20' THEN 'WARNING'
          WHEN severity = '30' THEN 'ERROR'
          WHEN severity = '40' THEN 'SEVERE'
     END);

-- channels (delete multitenancy / update pk)
ALTER TABLE messaging.channel DROP COLUMN tenant_code;

ALTER TABLE messaging.message DROP CONSTRAINT message_channel_id_channel_id_fk;
ALTER TABLE messaging.message RENAME channel_id TO channel_code;
ALTER TABLE messaging.message ALTER COLUMN channel_code TYPE VARCHAR;
COMMENT ON COLUMN messaging.message.channel_code IS 'Код канала отправки уведомления';

ALTER TABLE messaging.message_template DROP CONSTRAINT message_template_channel_id_channel_id_fk;
ALTER TABLE messaging.message_template RENAME channel_id TO channel_code;
ALTER TABLE messaging.message_template ALTER COLUMN channel_code TYPE VARCHAR;
COMMENT ON COLUMN messaging.message_template.channel_code IS 'Код канала отправки уведомления';

ALTER TABLE messaging.channel DROP CONSTRAINT channel_pkey;
ALTER TABLE messaging.channel ADD COLUMN code VARCHAR PRIMARY KEY;
COMMENT ON COLUMN messaging.channel.code IS 'Код канала';
ALTER TABLE messaging.channel DROP COLUMN id;

ALTER TABLE messaging.message ADD CONSTRAINT message_channel_code_channel_code_fk
    FOREIGN KEY (channel_code) REFERENCES messaging.channel (code);
ALTER TABLE messaging.message_template ADD CONSTRAINT message_template_channel_code_channel_code_fk
    FOREIGN KEY (channel_code) REFERENCES messaging.channel (code);




