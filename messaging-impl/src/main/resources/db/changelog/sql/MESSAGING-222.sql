ALTER TABLE messaging.message_recipient RENAME COLUMN read_at TO status_time;

COMMENT
ON COLUMN messaging.message_recipient.status_time IS 'Время установки статуса';