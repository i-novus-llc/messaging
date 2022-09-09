ALTER TABLE messaging.message DROP CONSTRAINT IF EXISTS message_recipient_type_check;
ALTER TABLE messaging.message ADD CONSTRAINT message_recipient_type_check CHECK (recipient_type IN ('RECIPIENT','USER_GROUP_BY_ROLE', 'USER_GROUP_BY_REGION', 'USER_GROUP_BY_ORGANIZATION', 'ALL'));

ALTER TABLE messaging.message ADD COLUMN organization VARCHAR;
ALTER TABLE messaging.message ADD COLUMN role VARCHAR;
ALTER TABLE messaging.message ADD COLUMN region VARCHAR;
COMMENT ON COLUMN messaging.message.organization IS 'Организации, сотрудникам которых отправлено уведомление';
COMMENT ON COLUMN messaging.message.role IS 'Роли, которым отправлено уведомление';
COMMENT ON COLUMN messaging.message.region IS 'Регион, сотрудникам которого отправлено уведомление';