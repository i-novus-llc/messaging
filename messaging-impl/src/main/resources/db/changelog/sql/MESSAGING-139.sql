CREATE TABLE messaging.system
(
    code VARCHAR PRIMARY KEY NOT NULL,
    name VARCHAR
);

COMMENT ON TABLE messaging.system IS 'Системы';
COMMENT ON COLUMN messaging.system.code IS 'Уникальный код системы';
COMMENT ON COLUMN messaging.system.name IS 'Наименование системы';

ALTER TABLE messaging.message
    RENAME COLUMN system_id TO system_code;
ALTER TABLE messaging.message
    ADD CONSTRAINT message_system_code_fkey
        FOREIGN KEY (system_code) REFERENCES messaging.system (code);
COMMENT ON COLUMN messaging.message.system_code IS 'Система, к которой относится уведомление';

ALTER TABLE messaging.message_setting
    ADD COLUMN system_code VARCHAR REFERENCES messaging.system (code);
COMMENT ON COLUMN messaging.message_setting.system_code IS 'Система, к которой относится настройка';

ALTER TABLE messaging.user_setting
    ADD COLUMN system_code VARCHAR REFERENCES messaging.system (code);
COMMENT ON COLUMN messaging.user_setting.system_code IS 'Система, к которой относится пользовательская настройка';