CREATE TABLE messaging.tenant
(
    code VARCHAR PRIMARY KEY NOT NULL,
    name VARCHAR
);

COMMENT ON TABLE messaging.tenant IS 'Тенанты';
COMMENT ON COLUMN messaging.tenant.code IS 'Уникальный код тенанта';
COMMENT ON COLUMN messaging.tenant.name IS 'Наименование тенанта';

ALTER TABLE messaging.message
    RENAME COLUMN system_id TO tenant_code;
ALTER TABLE messaging.message
    ADD CONSTRAINT message_tenant_code_fkey
        FOREIGN KEY (tenant_code) REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.message.tenant_code IS 'Тенант, к которому относится уведомление';

ALTER TABLE messaging.message_setting
    ADD COLUMN tenant_code VARCHAR REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.message_setting.tenant_code IS 'Тенант, к которому относится настройка';

ALTER TABLE messaging.user_setting
    ADD COLUMN tenant_code VARCHAR REFERENCES messaging.tenant (code);
COMMENT ON COLUMN messaging.user_setting.tenant_code IS 'Тенант, к которому относится пользовательская настройка';