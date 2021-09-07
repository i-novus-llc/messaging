CREATE TABLE channel
(
    id         VARCHAR PRIMARY KEY,
    name       VARCHAR NOT NULL,
    queue_name VARCHAR NOT NULL
);

COMMENT ON TABLE channel IS 'Канал отправки';
COMMENT ON COLUMN channel.id IS 'Уникальный код канала';
COMMENT ON COLUMN channel.name IS 'Имя канала для отображения на UI';
COMMENT ON COLUMN channel.queue_name IS 'Имя очереди канала';

ALTER TABLE message DROP COLUMN send_email;
ALTER TABLE message DROP COLUMN send_notice;
ALTER TABLE message ADD COLUMN channel_id VARCHAR;
ALTER TABLE message ADD CONSTRAINT message_channel_id_channel_id_fk FOREIGN KEY (channel_id) REFERENCES channel (id);

COMMENT ON COLUMN message.channel_id IS 'Идентификатор канала отправки';

ALTER TABLE message_setting DROP COLUMN send_email;
ALTER TABLE message_setting DROP COLUMN send_notice;
ALTER TABLE message_setting ADD COLUMN channel_id VARCHAR;
ALTER TABLE message_setting ADD CONSTRAINT message_setting_channel_id_channel_id_fk FOREIGN KEY (channel_id) REFERENCES channel (id);

COMMENT ON COLUMN message_setting.channel_id IS 'Идентификатор канала отправки';

ALTER TABLE user_setting DROP COLUMN send_email;
ALTER TABLE user_setting DROP COLUMN send_notice;
ALTER TABLE user_setting ADD COLUMN channel_id VARCHAR;
ALTER TABLE user_setting ADD CONSTRAINT user_setting_channel_id_channel_id_fk FOREIGN KEY (channel_id) REFERENCES channel (id);

COMMENT ON COLUMN user_setting.channel_id IS 'Идентификатор канала отправки';