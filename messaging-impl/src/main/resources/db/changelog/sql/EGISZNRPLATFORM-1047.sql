CREATE TABLE attachment (
    id           UUID PRIMARY KEY
  , message_id   UUID NOT NULL
  , file         VARCHAR NOT NULL
  , created_at   TIMESTAMP
    );

COMMENT ON TABLE attachment IS 'Вложения';
COMMENT ON COLUMN attachment.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN attachment.message_id IS 'Уникальный идентификатор уведомления';
COMMENT ON COLUMN attachment.file IS 'Имя файла в хранилище';
COMMENT ON COLUMN attachment.created_at IS 'Время создания файла';

CREATE SEQUENCE attachment_id_seq;

ALTER TABLE messaging.attachment ADD CONSTRAINT attachment_message_id_fk FOREIGN KEY (message_id) REFERENCES message(id);