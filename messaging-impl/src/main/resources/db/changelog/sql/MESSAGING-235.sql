DROP TABLE messaging.user_setting;
DROP SEQUENCE IF EXISTS messaging.user_setting_id_seq;

ALTER TABLE messaging.message_setting RENAME TO message_template;
COMMENT ON TABLE messaging.message_template IS 'Шаблоны уведомлений';
ALTER SEQUENCE messaging.message_setting_id_seq RENAME TO message_template_id_seq;
ALTER SEQUENCE messaging.recipient_id_seq RENAME TO message_recipient_id_seq;
