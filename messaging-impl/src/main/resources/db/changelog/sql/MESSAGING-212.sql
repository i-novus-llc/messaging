ALTER TABLE messaging.channel ADD COLUMN is_internal BOOLEAN;

COMMENT ON COLUMN messaging.channel.is_internal IS 'Признак внутрисистемного канала отправки';