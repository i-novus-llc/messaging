CREATE TABLE messaging.attachment (
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

CREATE SEQUENCE IF NOT EXISTS messaging.attachment_id_seq OWNED BY messaging.attachment.id;

ALTER TABLE messaging.attachment ADD CONSTRAINT attachment_message_id_fk FOREIGN KEY (message_id) REFERENCES messaging.message(id);

INSERT INTO messaging.channel (code, name, queue_name, is_internal) VALUES ('email', 'Email', 'email-queue', false);
INSERT INTO messaging.channel (code, name, queue_name, is_internal) VALUES ('web', 'Web', 'web-queue', true);