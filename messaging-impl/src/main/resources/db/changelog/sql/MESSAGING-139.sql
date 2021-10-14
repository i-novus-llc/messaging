-- tenant
CREATE TABLE messaging.tenant
(
    code VARCHAR PRIMARY KEY NOT NULL,
    name VARCHAR
);

COMMENT ON TABLE messaging.tenant IS 'Тенанты';
COMMENT ON COLUMN messaging.tenant.code IS 'Уникальный код тенанта';
COMMENT ON COLUMN messaging.tenant.name IS 'Наименование тенанта';

-- message
ALTER TABLE messaging.message
    RENAME COLUMN system_id TO tenant_code;
ALTER TABLE messaging.message
    ADD CONSTRAINT message_tenant_code_fkey
        FOREIGN KEY (tenant_code) REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.message.tenant_code IS 'Тенант, к которому относится уведомление';

ALTER TABLE messaging.message DROP COLUMN object_id;
ALTER TABLE messaging.message DROP COLUMN object_type;
ALTER TABLE messaging.message DROP COLUMN component_id;

-- message_setting
ALTER TABLE messaging.message_setting
    ADD COLUMN tenant_code VARCHAR REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.message_setting.tenant_code IS 'Тенант, к которому относится настройка';

ALTER TABLE messaging.message_setting DROP COLUMN component_id;

-- user_setting
ALTER TABLE messaging.user_setting
    ADD COLUMN tenant_code VARCHAR REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.user_setting.tenant_code IS 'Тенант, к которому относится пользовательская настройка';

-- channel
ALTER TABLE messaging.channel
    ADD COLUMN tenant_code VARCHAR REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.channel.tenant_code IS 'Тенант, к которому относится канал отправки';

-- add new pk
ALTER TABLE messaging.message DROP CONSTRAINT message_channel_id_channel_id_fk;
ALTER TABLE messaging.message_setting DROP CONSTRAINT message_setting_channel_id_channel_id_fk;
ALTER TABLE messaging.user_setting DROP CONSTRAINT user_setting_channel_id_channel_id_fk;

ALTER TABLE messaging.channel ALTER COLUMN id TYPE INTEGER USING id::INTEGER;

ALTER TABLE messaging.message ALTER COLUMN channel_id TYPE INTEGER USING channel_id::INTEGER;
ALTER TABLE messaging.message_setting ALTER COLUMN channel_id TYPE INTEGER USING channel_id::INTEGER;
ALTER TABLE messaging.user_setting ALTER COLUMN channel_id TYPE INTEGER USING channel_id::INTEGER;

ALTER TABLE messaging.message ADD CONSTRAINT message_channel_id_channel_id_fk
    FOREIGN KEY (channel_id) REFERENCES messaging.channel (id);
ALTER TABLE messaging.message_setting ADD CONSTRAINT message_setting_channel_id_channel_id_fk
    FOREIGN KEY (channel_id) REFERENCES messaging.channel (id);
ALTER TABLE messaging.user_setting ADD CONSTRAINT user_setting_channel_id_channel_id_fk
    FOREIGN KEY (channel_id) REFERENCES messaging.channel (id);

-- component
DROP TABLE IF EXISTS messaging.component;
