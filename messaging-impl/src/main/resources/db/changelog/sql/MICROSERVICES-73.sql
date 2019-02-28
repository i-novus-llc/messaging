ALTER TABLE message DROP COLUMN recipient;
ALTER TABLE message ADD COLUMN recipient_type VARCHAR;
UPDATE message SET recipient_type = 'USER';
ALTER TABLE message ALTER COLUMN recipient_type SET NOT NULL;
ALTER TABLE message ADD CONSTRAINT message_recipient_type_check CHECK (recipient_type IN ('ALL','USER'));
