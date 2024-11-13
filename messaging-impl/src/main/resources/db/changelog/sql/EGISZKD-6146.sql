ALTER TABLE messaging.recipient_group ADD COLUMN IF NOT EXISTS code VARCHAR UNIQUE;
COMMENT ON COLUMN messaging.recipient_group.code IS 'Код группы';

UPDATE messaging.recipient_group
SET code=id WHERE code is null;

ALTER TABLE messaging.recipient_group
    ALTER COLUMN code SET not null;