CREATE TABLE message (
    id           INTEGER PRIMARY KEY
  , caption      VARCHAR
  , text         VARCHAR NOT NULL
  , severity     VARCHAR NOT NULL CHECK (severity IN ('ERROR', 'WARNING', 'SEVERE', 'INFO', 'TRACE', 'DEBUG'))
  , alert_type   VARCHAR NOT NULL CHECK (alert_type IN ('BLOCKER', 'POPUP', 'HIDDEN'))
  , sent_at      TIMESTAMP
  , read_at      TIMESTAMP
  , recipient    VARCHAR
    );

COMMENT ON TABLE message IS 'Сообщения';
COMMENT ON COLUMN message.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN message.caption IS 'Заголовок';
COMMENT ON COLUMN message.text IS 'Содержимое сообщения';
COMMENT ON COLUMN message.severity IS 'Жесткость сообщения';
COMMENT ON COLUMN message.alert_type IS 'Тип предупреждения';
COMMENT ON COLUMN message.sent_at IS 'Отправлено (дата и время)';
COMMENT ON COLUMN message.read_at IS 'Помечено прочтенным (дата и время)';
COMMENT ON COLUMN message.recipient IS 'Получатель';

CREATE SEQUENCE message_id_seq;
