CREATE TABLE IF NOT EXISTS messaging.recipient_group
(
    id          INTEGER PRIMARY KEY,
    name        VARCHAR NOT NULL UNIQUE,
    description VARCHAR,
    tenant_code VARCHAR REFERENCES messaging.tenant (code)
);

CREATE SEQUENCE IF NOT EXISTS messaging.recipient_group_id_seq;

COMMENT ON TABLE messaging.recipient_group IS 'Группы получателей';
COMMENT ON COLUMN messaging.recipient_group.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN messaging.recipient_group.name IS 'Наименование группы';
COMMENT ON COLUMN messaging.recipient_group.description IS 'Описание группы';

CREATE TABLE IF NOT EXISTS messaging.recipient_group_user
(
    id                 SERIAL PRIMARY KEY,
    recipient_group_id INTEGER NOT NULL REFERENCES messaging.recipient_group (id),
    recipient_name     VARCHAR NOT NULL,
    recipient_username VARCHAR NOT NULL
);

CREATE INDEX IF NOT EXISTS recipient_group_user_recipient_group_id_idx ON messaging.recipient_group_user (recipient_group_id);

COMMENT ON TABLE messaging.recipient_group_user IS 'Связь получателей с группой';
COMMENT ON COLUMN messaging.recipient_group_user.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN messaging.recipient_group_user.recipient_group_id IS 'Ссылка на группу';
COMMENT ON COLUMN messaging.recipient_group_user.recipient_name IS 'Имя получателя';
COMMENT ON COLUMN messaging.recipient_group_user.recipient_username IS 'ID получателя';

CREATE TABLE IF NOT EXISTS messaging.recipient_group_template
(
    id                  SERIAL PRIMARY KEY,
    recipient_group_id  INTEGER NOT NULL REFERENCES messaging.recipient_group (id) ,
    message_template_id INTEGER NOT NULL REFERENCES messaging.message_template (id),
    message_template_code VARCHAR NOT NULL REFERENCES messaging.message_template (code)
);

CREATE INDEX IF NOT EXISTS recipient_group_template_recipient_group_id_idx ON messaging.recipient_group_template (recipient_group_id);

COMMENT ON TABLE messaging.recipient_group_template IS 'Связь шаблонов с группой';
COMMENT ON COLUMN messaging.recipient_group_template.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN messaging.recipient_group_template.recipient_group_id IS 'Ссылка на группу';
COMMENT ON COLUMN messaging.recipient_group_template.message_template_id IS 'Ссылка на шаблон';
COMMENT ON COLUMN messaging.recipient_group_template.message_template_code IS 'Код на шаблона';
