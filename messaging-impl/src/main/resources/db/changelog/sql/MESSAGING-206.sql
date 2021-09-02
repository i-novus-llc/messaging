ALTER TABLE message DROP COLUMN send_email;
ALTER TABLE message DROP COLUMN send_notice;
ALTER TABLE message ADD COLUMN send_channel VARCHAR;

COMMENT ON COLUMN message.send_channel IS 'Канал отправки';

ALTER TABLE message_setting DROP COLUMN send_email;
ALTER TABLE message_setting DROP COLUMN send_notice;
ALTER TABLE message_setting ADD COLUMN send_channel VARCHAR;

COMMENT ON COLUMN message_setting.send_channel IS 'Канал отправки';

ALTER TABLE user_setting DROP COLUMN send_email;
ALTER TABLE user_setting DROP COLUMN send_notice;
ALTER TABLE user_setting ADD COLUMN send_channel VARCHAR;

COMMENT ON COLUMN user_setting.send_channel IS 'Канал отправки';