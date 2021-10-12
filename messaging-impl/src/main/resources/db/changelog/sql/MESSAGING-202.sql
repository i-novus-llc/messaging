CREATE SCHEMA IF NOT EXISTS messaging;

-- move table into messaging schema
ALTER TABLE message SET SCHEMA messaging;
ALTER TABLE component SET SCHEMA messaging;
ALTER TABLE recipient SET SCHEMA messaging;
ALTER TABLE message_setting SET SCHEMA messaging;
ALTER TABLE user_setting SET SCHEMA messaging;
ALTER TABLE channel SET SCHEMA messaging;

-- message table
COMMENT ON TABLE messaging.message IS 'Уведомления';
COMMENT ON COLUMN messaging.message.caption IS 'Заголовок уведомления';
COMMENT ON COLUMN messaging.message.text IS 'Содержимое уведомления';
COMMENT ON COLUMN messaging.message.severity IS 'Важность уведомления';
COMMENT ON COLUMN messaging.message.alert_type IS 'Способ отображения уведомления';
COMMENT ON COLUMN messaging.message.sent_at IS 'Дата и время отправки уведомления';
COMMENT ON COLUMN messaging.message.system_id IS 'Идентификатор системы, к которой относится уведомление';
COMMENT ON COLUMN messaging.message.component_id IS 'Идентификатор компонента (модуля, подсистемы), к которому относится уведомление';
COMMENT ON COLUMN messaging.message.formation_type IS 'Тип формирования уведомления';
COMMENT ON COLUMN messaging.message.recipient_type IS 'Тип получателя уведомления';
COMMENT ON COLUMN messaging.message.notification_type IS 'Код шаблона, который был использован для формирования уведомления';
COMMENT ON COLUMN messaging.message.object_id IS 'Идентификатор объекта, по которому было направлено уведомление';
COMMENT ON COLUMN messaging.message.object_type IS 'Тип объекта, по которому было направлено уведомление';
COMMENT ON COLUMN messaging.message.channel_id IS 'Идентификатор канала отправки уведомления';

ALTER TABLE messaging.message DROP COLUMN send_email_date;
ALTER TABLE messaging.message DROP COLUMN send_email_error;
ALTER TABLE messaging.message DROP CONSTRAINT message_alert_type_check;
ALTER TABLE messaging.message DROP CONSTRAINT message_formation_type_check;
ALTER TABLE messaging.message DROP CONSTRAINT message_recipient_type_check;
ALTER TABLE messaging.message DROP CONSTRAINT message_severity_check;

-- channel table
COMMENT ON TABLE messaging.channel IS 'Каналы отправки уведомлений';
COMMENT ON COLUMN messaging.channel.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN messaging.channel.name IS 'Наименование канала';
COMMENT ON COLUMN messaging.channel.queue_name IS 'Наименование очереди канала';

-- message_setting table
COMMENT ON COLUMN messaging.message_setting.caption IS 'Заголовок уведомления';
COMMENT ON COLUMN messaging.message_setting.text IS 'Содержимое уведомления';
COMMENT ON COLUMN messaging.message_setting.severity IS 'Важность уведомления';
COMMENT ON COLUMN messaging.message_setting.alert_type IS 'Способ отображения уведомления';
COMMENT ON COLUMN messaging.message_setting.component_id IS 'Идентификатор компонента (модуля, подсистемы), к которому относится уведомление';
COMMENT ON COLUMN messaging.message_setting.name IS 'Наименование шаблона уведомления';
COMMENT ON COLUMN messaging.message_setting.formation_type IS 'Тип формирования уведомления';
COMMENT ON COLUMN messaging.message_setting.code IS 'Код шаблона уведомления';
COMMENT ON COLUMN messaging.message_setting.channel_id IS 'Идентификатор канала отправки уведомления';

ALTER TABLE messaging.message_setting DROP CONSTRAINT message_setting_alert_type_check;
ALTER TABLE messaging.message_setting DROP CONSTRAINT message_setting_formation_type_check;
ALTER TABLE messaging.message_setting DROP CONSTRAINT message_setting_severity_check;

-- user_setting table
COMMENT ON COLUMN messaging.user_setting.alert_type IS 'Способ отображения уведомления';
COMMENT ON COLUMN messaging.user_setting.user_id IS 'Идентификатор пользователя, к которому относится настройка';
COMMENT ON COLUMN messaging.user_setting.msg_setting_id IS 'Идентификатор шаблона уведомления';
COMMENT ON COLUMN messaging.user_setting.channel_id IS 'Идентификатор канала отправки уведомления';

ALTER TABLE messaging.user_setting
    DROP CONSTRAINT user_setting_alert_type_check;

-- recipient table
ALTER TABLE messaging.recipient
    RENAME TO message_recipient;
ALTER TABLE messaging.message_recipient
    DROP COLUMN recipient;
ALTER TABLE messaging.message_recipient
    DROP COLUMN email;
ALTER TABLE messaging.message_recipient
    ADD COLUMN recipient_name VARCHAR;
ALTER TABLE messaging.message_recipient
    ADD COLUMN recipient_username VARCHAR;

COMMENT ON TABLE messaging.message_recipient IS 'Получатель уведомления';
COMMENT ON COLUMN messaging.message_recipient.message_id IS 'Идентификатор уведомления';
COMMENT ON COLUMN messaging.message_recipient.recipient_name IS 'Имя контакта получателя';
COMMENT ON COLUMN messaging.message_recipient.read_at IS 'Дата и время прочтения уведомления';
COMMENT ON COLUMN messaging.message_recipient.recipient_username IS 'Уникальное имя пользователя из провайдера';

ALTER SEQUENCE IF EXISTS message_id_seq SET SCHEMA messaging;
ALTER SEQUENCE IF EXISTS message_setting_id_seq SET SCHEMA messaging;
ALTER SEQUENCE IF EXISTS recipient_id_seq SET SCHEMA messaging;
ALTER SEQUENCE IF EXISTS user_setting_id_seq SET SCHEMA messaging;






