CREATE TABLE message_log (
    id           INTEGER PRIMARY KEY
  , message_id   INTEGER NOT NULL REFERENCES message(id)
  , recipient    VARCHAR NOT NULL
  , sent_at      TIMESTAMP
  , read_at      TIMESTAMP
    );

COMMENT ON TABLE message_log IS 'Лог сообщения';
COMMENT ON COLUMN message_log.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN message_log.message_id IS 'Ссылка на сообщение';
COMMENT ON COLUMN message_log.recipient IS 'Получатель';
COMMENT ON COLUMN message_log.sent_at IS 'Отправлено (дата и время)';
COMMENT ON COLUMN message_log.read_at IS 'Помечено прочтенным (дата и время)';
