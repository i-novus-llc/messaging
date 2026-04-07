ALTER TABLE messaging.message
    ADD COLUMN message_type VARCHAR;

COMMENT ON COLUMN messaging.message.message_type IS 'Тип уведомления'
