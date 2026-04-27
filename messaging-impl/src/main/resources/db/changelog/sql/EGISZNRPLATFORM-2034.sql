ALTER TABLE messaging.message ALTER COLUMN organization TYPE VARCHAR USING organization::VARCHAR;
COMMENT ON COLUMN messaging.message.organization IS 'Организации, сотрудникам которых отправлено уведомление';
