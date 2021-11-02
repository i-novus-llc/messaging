ALTER TABLE messaging.message
    ALTER COLUMN severity DROP NOT NULL;
ALTER TABLE messaging.message
    ALTER COLUMN alert_type DROP NOT NULL;

ALTER TABLE messaging.message_template
    ALTER COLUMN severity DROP NOT NULL;
ALTER TABLE messaging.message_template
    ALTER COLUMN alert_type DROP NOT NULL;