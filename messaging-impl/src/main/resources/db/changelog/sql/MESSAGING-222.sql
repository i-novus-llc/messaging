ALTER TABLE messaging.message_recipient RENAME COLUMN read_at TO status_time;
COMMENT
ON TABLE messaging.message IS 'Время установки статуса';