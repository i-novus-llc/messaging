CREATE TABLE IF NOT EXISTS messaging.attachment (
    id           UUID PRIMARY KEY
  , message_id   UUID NOT NULL
  , file         VARCHAR NOT NULL
  , created_at   TIMESTAMP
    );

COMMENT ON TABLE messaging.attachment IS 'Вложения';
COMMENT ON COLUMN messaging.attachment.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN messaging.attachment.message_id IS 'Уникальный идентификатор уведомления';
COMMENT ON COLUMN messaging.attachment.file IS 'Имя файла в хранилище';
COMMENT ON COLUMN messaging.attachment.created_at IS 'Время создания файла';

DROP SEQUENCE IF EXISTS messaging.attachment_id_seq;

ALTER TABLE messaging.attachment DROP CONSTRAINT IF EXISTS attachment_message_id_fk;
ALTER TABLE messaging.attachment ADD CONSTRAINT attachment_message_id_fk FOREIGN KEY (message_id) REFERENCES messaging.message(id);