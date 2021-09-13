ALTER TABLE messaging.message_recipient ADD COLUMN status VARCHAR;
ALTER TABLE messaging.message_recipient ADD COLUMN departured_at TIMESTAMP;
ALTER TABLE messaging.message_recipient ADD COLUMN send_message_error VARCHAR;

COMMENT ON COLUMN messaging.message_recipient.status IS 'Текущий статус отправки уведомления получателю';
COMMENT ON COLUMN messaging.message_recipient.departured_at IS 'Дата и время фактической отправки уведомления';
COMMENT ON COLUMN messaging.message_recipient.send_message_error IS 'Сообщение ошибки отправки уведомления';