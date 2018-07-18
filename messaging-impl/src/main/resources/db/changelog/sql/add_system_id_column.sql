ALTER TABLE message ADD COLUMN system_id VARCHAR;
UPDATE message SET system_id = 'default';
ALTER TABLE message ALTER COLUMN system_id SET NOT NULL;
COMMENT ON COLUMN message.system_id IS 'Идентификатор системы';
CREATE INDEX ix_message_system_id ON message USING btree(system_id);
CREATE INDEX ix_message_recipient ON message USING btree(recipient);
