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

ALTER TABLE message
    DROP COLUMN send_email;
ALTER TABLE message
    DROP COLUMN send_notice;
ALTER TABLE message
    ADD COLUMN send_channel VARCHAR;
ALTER TABLE message
    ADD CONSTRAINT channel_code_fk FOREIGN KEY (send_channel) REFERENCES channel (id);

COMMENT ON COLUMN message.send_channel IS 'Канал отправки';

ALTER TABLE message_setting
    DROP COLUMN send_email;
ALTER TABLE message_setting
    DROP COLUMN send_notice;
ALTER TABLE message_setting
    ADD COLUMN send_channel VARCHAR;
ALTER TABLE message_setting
    ADD CONSTRAINT channel_code_fk FOREIGN KEY (send_channel) REFERENCES channel (id);

COMMENT ON COLUMN message_setting.send_channel IS 'Канал отправки';

ALTER TABLE user_setting
    DROP COLUMN send_email;
ALTER TABLE user_setting
    DROP COLUMN send_notice;
ALTER TABLE user_setting
    ADD COLUMN send_channel VARCHAR;
ALTER TABLE user_setting
    ADD CONSTRAINT channel_code_fk FOREIGN KEY (send_channel) REFERENCES channel (id);

COMMENT ON COLUMN user_setting.send_channel IS 'Канал отправки';