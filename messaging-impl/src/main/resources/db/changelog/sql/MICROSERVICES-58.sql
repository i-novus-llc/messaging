ALTER TABLE message DROP CONSTRAINT message_pk;
ALTER TABLE message ADD CONSTRAINT message_pkey PRIMARY KEY (id);

-- Компонент системы

CREATE TABLE component (
    id INTEGER PRIMARY KEY
  , name VARCHAR
);

COMMENT ON TABLE component IS 'Компоненты системы';
COMMENT ON COLUMN component.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN component.name IS 'Наименование компонента';

-- Шаблоны (общесистемные настройки)

CREATE TABLE message_setting (
    id           INTEGER PRIMARY KEY
  , caption      VARCHAR
  , text         VARCHAR NOT NULL
  , severity     VARCHAR NOT NULL CHECK (severity IN ('ERROR', 'WARNING', 'SEVERE', 'INFO', 'TRACE', 'DEBUG'))
  , alert_type   VARCHAR NOT NULL CHECK (alert_type IN ('BLOCKER', 'POPUP', 'HIDDEN'))
  , info_type    VARCHAR NOT NULL CHECK (info_type IN ('ALL', 'NOTICE', 'EMAIL'))
  , component_id INTEGER REFERENCES component(id)
  , name         VARCHAR
  , is_disabled  BOOLEAN DEFAULT FALSE
  , formation_type VARCHAR NOT NULL CHECK (formation_type IN ('AUTO', 'HAND'))
);

COMMENT ON TABLE  message_setting IS 'Шаблоны уведомлений (общесистемные настройки)';
COMMENT ON COLUMN message_setting.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN message_setting.caption IS 'Заголовок';
COMMENT ON COLUMN message_setting.text IS 'Содержимое сообщения';
COMMENT ON COLUMN message_setting.severity IS 'Жесткость сообщения';
COMMENT ON COLUMN message_setting.alert_type IS 'Тип предупреждения';
COMMENT ON COLUMN message_setting.info_type IS 'Вид информирования по событию (почта, центр уведомления)';
COMMENT ON COLUMN message_setting.component_id IS 'Ссылка на системный справочник компонентов Системы. Системный справочник будет настраиваться для каждой Системы при необходимости';
COMMENT ON COLUMN message_setting.name IS 'Название шаблона (события)';
COMMENT ON COLUMN message_setting.is_disabled IS 'Признак выключения уведомления';
COMMENT ON COLUMN message_setting.formation_type IS 'Тип формирования уведомления';

CREATE SEQUENCE message_setting_id_seq;

-- Пользовательские настройки

CREATE TABLE user_setting (
    id           INTEGER PRIMARY KEY
  , alert_type   VARCHAR NOT NULL CHECK (alert_type IN ('BLOCKER', 'POPUP', 'HIDDEN'))
  , info_type    VARCHAR NOT NULL CHECK (info_type IN ('ALL', 'NOTICE', 'EMAIL'))
  , is_disabled  BOOLEAN DEFAULT FALSE
  , user_id      VARCHAR NOT NULL
);

COMMENT ON TABLE  user_setting IS 'Пользовательские настройки уведомлений';
COMMENT ON COLUMN user_setting.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN user_setting.alert_type IS 'Тип предупреждения';
COMMENT ON COLUMN user_setting.info_type IS 'Вид информирования по событию (почта, центр уведомления)';
COMMENT ON COLUMN user_setting.is_disabled IS 'Признак выключения уведомления';
COMMENT ON COLUMN user_setting.user_id IS 'Идентификатор пользователя';

-- Получатели сообщения

CREATE TABLE recipient (
    id         INTEGER PRIMARY KEY
  , recipient  VARCHAR NOT NULL
  , message_id VARCHAR NOT NULL REFERENCES message(id)
  , read_at    TIMESTAMP
);

COMMENT ON TABLE  recipient IS 'Получатели сообщения';
COMMENT ON COLUMN recipient.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN recipient.recipient IS 'Получатель';
COMMENT ON COLUMN recipient.message_id IS 'Ссылка на сообщение';
COMMENT ON COLUMN recipient.read_at IS 'Помечено прочтенным (дата и время)';

CREATE SEQUENCE recipient_id_seq;

-- Изменения в таблице message

ALTER TABLE message ADD COLUMN info_type VARCHAR NOT NULL CHECK (info_type IN ('ALL', 'NOTICE', 'EMAIL')) DEFAULT 'NOTICE';
COMMENT ON COLUMN message.info_type IS 'Вид информирования по событию (почта, центр уведомления)';

ALTER TABLE message ADD COLUMN component_id INTEGER REFERENCES component(id);
COMMENT ON COLUMN message.component_id IS 'Компонент Системы, к которому относится уведомление';

ALTER TABLE message ADD COLUMN formation_type VARCHAR NOT NULL CHECK (formation_type IN ('AUTO', 'HAND')) DEFAULT 'AUTO';
COMMENT ON COLUMN message.formation_type IS 'Тип формирования уведомления';
